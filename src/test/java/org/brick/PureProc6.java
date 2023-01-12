package org.brick;

import org.brick.core.IPureProcess;

public class PureProc6 implements IPureProcess<Integer, Integer, Integer> {

    @Override
    public Integer pureCalculate(Integer input, Integer context) {
        System.out.println("Proc5 input: " + input);
        System.out.println("Proc5 output: " + (input + 1));
        return input + 1;
    }
}
