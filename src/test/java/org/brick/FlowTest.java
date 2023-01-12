package org.brick;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlowTest {

    @Test
    public void testFlow() {

        Integer a = 10;
        new FlowMaker<String, String, Integer>()
                .newFlowWithContext(a)
                .next(IPureProcess.class, new PureProc1())
                .next(IPureProcess.class, new PureProc2())
                .next(IYesNoBranchProcess.class, new IYesNoBranch<String, String, Integer>(
                        (i, c) -> StringUtils.equals(i, "yes"),
                        FlowHelper.fromPure(new PureProc3()),
                        FlowHelper.fromPure(new PureProc4())))
                .next(IParallelProcess.class, new ParallelProcess<>((s, c) -> List.of(1, 2, 3, 4),
                        (e, c) -> e % 2 == 0,
                        (e, c) -> e * 2,
                        Collectors.summingInt(i -> i)))
                .last(IPureProcess.class, new PureProc5())
                .build().run("hello");
    }
}
