package org.brick;

import org.junit.Test;

public class FlowTest {

    @Test
    public void testFlow() {

        Integer a = 10;
        new FlowMaker<String, String, Integer>()
                .newFlowWithContext(a)
                .nextPure(new PureProc1())
                .nextPure(new PureProc2())
                .nextYesNoBranch(new YesNoBranch1())
                .build().run("hello");
    }
}
