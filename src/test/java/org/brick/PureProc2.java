package org.brick;

public class PureProc2 implements PureProcess<Integer, String, Integer>{
    @Override
    public String process(Integer input, Integer context) {
        System.out.println("Proc2 input: " + input);
        System.out.println("Proc2 output: yes");
        return "yes";
    }
}
