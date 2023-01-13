package org.brick;

import org.brick.core.IPureProcess;

public class PureProc7 implements IPureProcess<Integer, Integer, Integer> {
    @Override
    public Integer pureCalculate(Integer input, Integer context) {
        System.out.println(String.format("Proc7 input: %d, context: %d", input, context));
        System.out.println(String.format("Proc7 output: %d", input + 1));
        return input + 1;
    }
}
