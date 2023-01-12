package org.brick;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FlowTest {

    @Test
    public void testFlow() {

        Integer a = 10;

        IFlow<Integer, String, Integer> subFlow = new FlowMaker<Integer, String, Integer>(Executors.newSingleThreadExecutor())
                .builder()
                .last(IPureProcess.class, new PureProc5())
                .build();
        String result = new FlowMaker<String, String, Integer>(Executors.newSingleThreadExecutor())
                .builder()
                .next(IPureProcess.class, new PureProc1())
                .next(IPureProcess.class, new PureProc2())
                .next(IYesNoBranchFlow.class, new IYesNoBranch<String, String, Integer>(
                        (i, c) -> StringUtils.equals(i, "yes"),
                        FlowHelper.fromPure(new PureProc3()),
                        FlowHelper.fromPure(new PureProc4())))
                .async(new AsyncProc1())
                .next(IParallelFlow.class, new ParallelProcess<>((s, c) -> List.of(1, 2, 3, 4),
                        (e, c) -> e % 2 == 0,
                        (e, c) -> e * 2,
                        Collectors.summingInt(i -> i)))
                .last(IFlow.class, subFlow)
                .build()
                .run("hello", a);

    }
}
