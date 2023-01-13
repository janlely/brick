package org.brick;

import org.brick.core.FlowDoc;
import org.brick.core.IPureFunction;

public class PureProc6 implements IPureFunction<Integer, Integer, Integer> {

    private String desc;

    public PureProc6(String desc) {
        this.desc = desc;
    }


    @Override
    public Integer pureCalculate(Integer input, Integer context) {
        System.out.println(String.format("Proc5 input: %d, context: %d", input, context));
        System.out.println(String.format("Proc5 output: %d", input + 1));
        return input + 1;
    }

    @Override
    public FlowDoc<Integer, Integer, Integer> getFlowDoc() {
        FlowDoc<Integer,Integer,Integer> flowDoc = new FlowDoc<>(this.desc);
        return flowDoc.types(Integer.class,Integer.class, Integer.class);
    }
}
