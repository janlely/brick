package org.brick;

import org.brick.core.IPureProcess;

public class PureProc5 implements IPureProcess<Integer, String, Integer> {

    @Override
    public String pureCalculate(Integer input, Integer context) {
        System.out.println(String.format("Proc5 input: %d, context: %d", input, context));
        System.out.println(String.format("Proc5 output: %s", input + 1));
        return String.valueOf(input + 1);
    }
}
