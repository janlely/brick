package org.brick;

import org.brick.core.IPureProcess;

public class PureProc2 implements IPureProcess<Integer, String, Integer> {

    @Override
    public String pureCalculate(Integer input, Integer context) {
        System.out.println(String.format("Proc2 input: %d, context: %d", input, context));
        System.out.println(String.format("Proc2 output: yes"));
        return "yes";
    }
}
