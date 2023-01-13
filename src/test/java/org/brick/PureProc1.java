package org.brick;

import org.brick.core.IPureProcess;

public class PureProc1 implements IPureProcess<String, Integer, Integer> {

    @Override
    public Integer pureCalculate(String input, Integer context) {
        System.out.println(String.format("Proc1 input: %s, context: %d", input, context));
        System.out.println(String.format("Proc1 output: %s", context + 1));
        return context + 1;
    }
}
