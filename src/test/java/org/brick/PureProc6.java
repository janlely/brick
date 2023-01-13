package org.brick;

import org.brick.core.IPureProcess;

public class PureProc6 implements IPureProcess<Integer, Integer, Integer> {

    @Override
    public Integer pureCalculate(Integer input, Integer context) {
        System.out.println(String.format("Proc5 input: %d, context: %d", input, context));
        System.out.println(String.format("Proc5 output: %d", input + 1));
        return input + 1;
    }
}
