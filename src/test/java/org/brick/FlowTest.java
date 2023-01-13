package org.brick;

import org.apache.commons.lang3.StringUtils;
import org.brick.core.CaseFlow;
import org.brick.core.Flow;
import org.brick.core.FlowMaker;
import org.brick.core.IMultiBranch;
import org.brick.core.IPureProcess;
import org.brick.core.IYesNoBranchFlow;
import org.brick.model.CaseBatch;
import org.brick.model.IModifyCachePureProcess;
import org.brick.model.IModifyDBPureProcess;
import org.brick.model.IParallelFlow;
import org.brick.model.IYesNoBranch;
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
                .builder()
                .last(IPureProcess.class, new PureProc5())
                .build();
        Flow<Integer, Integer, Integer> case1 = new FlowMaker<Integer, Integer, Integer>(Executors.newSingleThreadExecutor())
                .builder()
                .last(IPureProcess.class, new PureProc6())
                .build();
        Flow<Integer, Integer, Integer> case2 = new FlowMaker<Integer, Integer, Integer>(Executors.newSingleThreadExecutor())
                .builder()
                .last(IPureProcess.class, new PureProc7())
                .build();
        IModifyDBPureProcess<Integer, Integer> modifyDBPure = new IModifyDBPureProcess<>() {

            @Override
            public void doDBModifyPure(Integer input, Integer context) {
                System.out.println("updating table brick_test");
            }

            @Override
            public String getPseudoSql() {
                return "UPDATE brick_text set user = ?";
            }
        };

        String result = new FlowMaker<String, String, Integer>(Executors.newSingleThreadExecutor())
                .builder()
                .pure(IPureProcess.class, new PureProc1())
                .pure(IPureProcess.class, new PureProc2())
                .subFlow(IYesNoBranchFlow.class, new IYesNoBranch<String, String, Integer>(
                        (i, c) -> StringUtils.equals(i, "yes"),
                        FlowHelper.fromPure(new PureProc3()),
                        FlowHelper.fromPure(new PureProc4())))
                .async(new AsyncProc1())
                .pure(IParallelFlow.class, new ParallelProcess<>((s, c) -> List.of(1, 2, 3, 4),
                        (e, c) -> e % 2 == 0,
                        (e, c) -> e * 2,
                        Collectors.summingInt(i -> i)))
                .subFlow(IMultiBranch.class, new CaseBatch<>(i -> i % 2 == 0 ? 1 : 2,
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
