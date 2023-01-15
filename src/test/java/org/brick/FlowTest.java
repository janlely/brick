package org.brick;

import org.apache.commons.lang3.StringUtils;
import org.brick.core.*;
import org.brick.core.AsyncFlow;
import org.brick.core.CaseBranch;
import org.brick.core.PureFunction;
import org.brick.core.YesNoBranch;
import org.brick.model.ModifyDBFlow;
import org.brick.model.ParallelFlow;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FlowTest {

    @Test
    public void testFlow() {

        Integer a = 10;

        Flow<Integer, String, Integer> subFlow = new FlowMaker<Integer, String, Integer>("Test Sub flow")
                .asyncExecutor(Executors.newSingleThreadExecutor())
                .flowBuilder()
                .pure(new PureFunction<>("PureFunction5", (i,c) -> {
                    System.out.println(String.format("PureFunction5 input: %d, context: %d", i, c));
                    return String.valueOf(i + 1);
                }))
                .finish()
                .build();
        Flow<Integer, Integer, Integer> case1 = new FlowMaker<Integer, Integer, Integer>("Test Case 1")
                .asyncExecutor(Executors.newSingleThreadExecutor())
                .flowBuilder()
                .pure(new PureFunction<>("PureProc6", (i,c) -> {
                    System.out.println(String.format("PureFunction6 input: %d, context: %d", i, c));
                    return i+1;
                }))
                .finish()
                .build();
        Flow<Integer, Integer, Integer> case2 = new FlowMaker<Integer, Integer, Integer>("Test case 2")
                .asyncExecutor(Executors.newSingleThreadExecutor())
                .flowBuilder()
                .pure(new PureFunction<>("PureProc7", (i,c) -> {
                    System.out.println(String.format("PureFunction7 input: %d, context: %d", i, c));
                    return i * 10;
                }))
                .finish()
                .build();

        String result = new FlowMaker<String, String, Integer>("Main Flow")
                .asyncExecutor(Executors.newSingleThreadExecutor())
                .flowBuilder()
                .pure(new PureFunction<>("PureFlow", (i,c) -> {
                    System.out.println(String.format("PureFunction7 input: %s, context: %d", i, c));
                    return c + 1;
                }))
                .pure(new PureFunction<>("PureFlow", (i,c) -> {
                    System.out.println(String.format("PureFunction7 input: %d, context: %d", i, c));
                    return String.valueOf(i*2);
                }))
                .pure(new PureFunction<>("Sample PureFunction",
                        (i,c) -> {
                            System.out.println("this is a sample PureFunction");
                            return "yes";
                        }))
                .subFlow(new YesNoBranch<>(
                        "Sample YesNoBranchFlow",
                        (i, c) -> StringUtils.equals(i, "yes"),
                        FlowHelper.fromPure(new PureFunction<>("PureFunction3", (i,c) -> {
                            System.out.println(String.format("PureFunction7 input: %s, context: %d", i, c));
                            return StringUtils.reverse(i);
                        })),
                        FlowHelper.fromPure(new PureFunction<>("PureFunction3", (i,c) -> {
                            System.out.println(String.format("PureFunction7 input: %s, context: %d", i, c));
                            return StringUtils.upperCase(i);
                        }))))
                .async(new AsyncFlow<>("Sample AsyncFlow",
                        (i,c) -> System.out.println("this is a AsyncFlow")))
                .pure(new ParallelFlow<>(
                        "Sample ParallelFlow",
                        (s, c) -> List.of(1, 2, 3, 4),
                        (e, c) -> e % 2 == 0,
                        (e, c) -> e * 2,
                        Collectors.summingInt(i -> i)))
                .subFlow(new CaseBranch<>(
                        "Sample CaseBranch",
                        i -> i % 2 == 0 ? 1 : 2,
                        new CaseFlow<>(1, case1),
                        new CaseFlow<>(2, case2)
                ))
                .effect(new ModifyDBFlow<>("this is a demo IModifyDBPureProcess",
                        "UPDATE table SET value = ?",
                        (i,c) -> {
                            System.out.println(String.format("IModifyDBPureProcess input: %d, context: %d", i, c));
                            return i;
                        }))
                .subFlow(subFlow)
                .finish()
                .build()
                .run("hello", a);
        System.out.println("result: " + result);
    }


    @Test
    public void testClassEqual() {
        System.out.println(Integer.class.equals(Integer.class));

    }



}
