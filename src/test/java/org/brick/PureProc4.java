package org.brick;

public class PureProc4 implements IPureProcess<String, String, Integer> {

    @Override
    public String pureCalculate(String input, Integer context) {
        System.out.println("Proc4 input: " + input);
        System.out.println("Proc4 output: " + input + " done");
        return input + " done";
    }
}
