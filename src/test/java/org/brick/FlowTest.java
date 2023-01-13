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
import org.brick.model.IModifyCachePureProcess;
import org.brick.model.IModifyDBPureProcess;
import org.brick.model.IParallelFlow;
import org.brick.model.PureFunction;
import org.brick.model.YesNoBranch;
import org.brick.model.ParallelProcess;
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
                .last(IPureFunction.class, new PureProc5("PureProc5"))
                .build();
        Flow<Integer, Integer, Integer> case1 = new FlowMaker<Integer, Integer, Integer>(Executors.newSingleThreadExecutor())
                .flowBuilder()
                .last(IPureFunction.class, new PureProc6("PureProc6"))
                .build();
        Flow<Integer, Integer, Integer> case2 = new FlowMaker<Integer, Integer, Integer>(Executors.newSingleThreadExecutor())
                .flowBuilder()
                .last(IPureFunction.class, new PureProc7("PureProc7"))
                .build();
        IModifyDBPureProcess<Integer, Integer> modifyDBPure = new IModifyDBPureProcess<>() {

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
                .pure(IPureFunction.class, new PureProc1("PureProc1"))
                .pure(IPureFunction.class, new PureProc2("PureProc2"))
                .pure(IPureFunction.class, new PureFunction<>("Sample PureFunction",
                        (i,c) -> {
                            System.out.println("this is a sample PureFunction");
                            return "yes";
                        }))
                .subFlow(IYesNoBranchFlow.class, new YesNoBranch<String, String, Integer>(
                        "Sample YesNoBranchFlow",
                        (i, c) -> StringUtils.equals(i, "yes"),
                        FlowHelper.fromPure(new PureProc3("PureProc3")),
                        FlowHelper.fromPure(new PureProc4("PureProc3"))))
                .async(new AsyncFlow<>("Sample AsyncProc",
                        (i,c) -> System.out.println("this is a AsyncFlow")))
                .pure(IParallelFlow.class, new ParallelProcess<>(
                        "Sample ParallelFlow",
                        (s, c) -> List.of(1, 2, 3, 4),
                        (e, c) -> e % 2 == 0,
                        (e, c) -> e * 2,
                        Collectors.summingInt(i -> i)))
                .subFlow(IMultiBranch.class, new CaseBranch<>(
                        "Sample CaseBranch",
                        i -> i % 2 == 0 ? 1 : 2,
                        new CaseFlow<>(1, case1),
                        new CaseFlow<>(2, case2)
                ))
                .effect(IModifyCachePureProcess.class, modifyDBPure)
                .last(subFlow)
                .build()
                .run("hello", a);
        System.out.println("result: " + result);
    }
}
