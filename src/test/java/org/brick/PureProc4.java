package org.brick;

import org.brick.core.IPureProcess;

public class PureProc4 implements IPureProcess<String, String, Integer> {

    @Override
    public String pureCalculate(String input, Integer context) {
        System.out.println(String.format("Proc4 input: %s, context: %d", input, context));
        System.out.println(String.format("Proc4 output: %s", input + " done"));
        return input + " done";
    }
}
