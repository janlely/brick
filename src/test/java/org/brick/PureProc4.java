package org.brick;

import org.brick.core.FlowDoc;
import org.brick.core.IPureFunction;

public class PureProc4 implements IPureFunction<String, String, Integer> {

    private String desc;

    public PureProc4(String desc) {
        this.desc = desc;
    }

    @Override
    public String pureCalculate(String input, Integer context) {
        System.out.println(String.format("Proc4 input: %s, context: %d", input, context));
        System.out.println(String.format("Proc4 output: %s", input + " done"));
        return input + " done";
    }

    @Override
    public FlowDoc<String, String, Integer> getFlowDoc() {
        FlowDoc<String,String,Integer> flowDoc = new FlowDoc<>(this.desc);
        return flowDoc.types(String.class,String.class, Integer.class);
    }
}
