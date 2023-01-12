package org.brick;

public class PureProc1 implements PureProcess<String, Integer, Integer>{
    @Override
    public Integer process(String input, Integer context) {
        System.out.println("Proc1 input: " + input);
        System.out.println("Proc1 output: " + context + 1);
        return context + 1;
    }
}
