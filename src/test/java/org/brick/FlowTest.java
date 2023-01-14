package org.brick;

import org.apache.commons.lang3.StringUtils;
import org.brick.core.CaseFlow;
import org.brick.core.Flow;
import org.brick.core.FlowDoc;
import org.brick.core.FlowHelper;
import org.brick.core.FlowMaker;
import org.brick.core.IMultiBranch;
import org.brick.core.IPureFunction;
import org.brick.core.IYesNoBranchFlow;
import org.brick.model.AsyncFlow;
import org.brick.model.CaseBranch;
import org.brick.model.IModifyCachePureFlow;
import org.brick.model.IModifyDBPureFlow;
import org.brick.model.IParallelFlow;
import org.brick.model.PureFunction;
import org.brick.model.YesNoBranch;
import org.brick.model.ParallelFlow;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FlowTest {

    @Test
    public void testFlow() {

        Integer a = 10;

        Flow<Integer, String, Integer> subFlow = new FlowMaker<Integer, String, Integer>(Executors.newSingleThreadExecutor())
                .flowBuilder()
                .last(IPureFunction.class, new PureFunction<>("PureProc5", (i,c) -> {
                    System.out.println(String.format("PureFunction5 input: %d, context: %d", i, c));
                    return String.valueOf(i + 1);
                }))
                .build();
        Flow<Integer, Integer, Integer> case1 = new FlowMaker<Integer, Integer, Integer>(Executors.newSingleThreadExecutor())
                .flowBuilder()
                .last(IPureFunction.class, new PureFunction<>("PureProc6", (i,c) -> {
                    System.out.println(String.format("PureFunction6 input: %d, context: %d", i, c));
                    return i+1;
                }))
                .build();
        Flow<Integer, Integer, Integer> case2 = new FlowMaker<Integer, Integer, Integer>(Executors.newSingleThreadExecutor())
                .flowBuilder()
                .last(IPureFunction.class, new PureFunction<>("PureProc7", (i,c) -> {
                    System.out.println(String.format("PureFunction7 input: %d, context: %d", i, c));
                    return i * 10;
                }))
                .build();
        IModifyDBPureFlow<Integer, Integer> modifyDBPure = new IModifyDBPureFlow<>() {

            @Override
            public void doDBModifyPure(Integer input, Integer context) {
                System.out.println(String.format("IModifyDBPureProcess input: %d, context: %d", input, context));
            }

            @Override
            public String getPseudoSql() {
                return "UPDATE table SET value = ?";
            }

            @Override
            public FlowDoc<Integer, Integer, Integer> getFlowDoc() {
                return new FlowDoc<Integer, Integer, Integer>("this is a demo IModifyDBPureProcess")
                        .types(Integer.class, Integer.class, Integer.class);
            }

            @Override
            public String getFlowType() {
                return "IModifyDBPureProcess";
            }
        };

        String result = new FlowMaker<String, String, Integer>(Executors.newSingleThreadExecutor())
                .withDesc("this is a test flow")
                .flowBuilder()
                .pure(new PureFunction<>("PureProc1", (i,c) -> {
                    System.out.println(String.format("PureFunction7 input: %s, context: %d", i, c));
                    return c + 1;
                }))
                .pure(new PureFunction<>("PureProc2", (i,c) -> {
                    System.out.println(String.format("PureFunction7 input: %d, context: %d", i, c));
                    return String.valueOf(i*2);
                }))
                .pure(new PureFunction<>("Sample PureFunction",
                        (i,c) -> {
                            System.out.println("this is a sample PureFunction");
                            return "yes";
                        }))
                .subFlow(new YesNoBranch<String, String, Integer>(
                        "Sample YesNoBranchFlow",
                        (i, c) -> StringUtils.equals(i, "yes"),
                        FlowHelper.fromPure(new PureFunction<>("PureProc3", (i,c) -> {
                            System.out.println(String.format("PureFunction7 input: %s, context: %d", i, c));
                            return StringUtils.reverse(i);
                        })),
                        FlowHelper.fromPure(new PureFunction<>("PureProc3", (i,c) -> {
                            System.out.println(String.format("PureFunction7 input: %s, context: %d", i, c));
                            return StringUtils.upperCase(i);
                        }))))
                .async(new AsyncFlow<>("Sample AsyncProc",
                        (i,c) -> System.out.println("this is a AsyncFlow")))
                .pure(new ParallelFlow<>(
                        "Sample ParallelFlow",
                        (s, c) -> List.of(1, 2, 3, 4),
                        (e, c) -> e % 2 == 0,
                        (e, c) -> e * 2,
                        Collectors.summingInt(i -> i)))
                .subFlow(new CaseBranch<Integer, Integer, Integer, Integer>(
                        "Sample CaseBranch",
                        i -> i % 2 == 0 ? 1 : 2,
                        new CaseFlow<>(1, case1),
                        new CaseFlow<>(2, case2)
                ))
                .effect(modifyDBPure)
                .last(subFlow)
                .build()
                .run("hello", a);
        System.out.println("result: " + result);
    }
}
