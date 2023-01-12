package org.brick;

public class PureProc2 implements IPureProcess<Integer, String, Integer> {

    @Override
    public String pureCalculate(Integer input, Integer context) {
        System.out.println("Proc2 input: " + input);
        System.out.println("Proc2 output: yes");
        return "yes";
    }
}
