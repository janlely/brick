package org.brick;

import org.brick.core.FlowDoc;
import org.brick.core.IPureFunction;

public class PureProc2 implements IPureFunction<Integer, String, Integer> {

    private String desc;

    public PureProc2(String desc) {
        this.desc = desc;
    }
    @Override
    public String pureCalculate(Integer input, Integer context) {
        System.out.println(String.format("Proc2 input: %d, context: %d", input, context));
        System.out.println(String.format("Proc2 output: yes"));
        return "yes";
    }

    @Override
    public FlowDoc<Integer, String, Integer> getFlowDoc() {
        FlowDoc<Integer,String,Integer> flowDoc = new FlowDoc<>(this.desc);
        return flowDoc.types(Integer.class,String.class, Integer.class);
    }
}
