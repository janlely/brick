package org.brick;

public class PureProc5 implements IPureProcess<Integer, String, Integer>{

    @Override
    public String pureCalculate(Integer input, Integer context) {
        System.out.println("Proc5 input: " + input);
        System.out.println("Proc5 output: " + (input + 1));
        return String.valueOf(input + 1);
    }
}
