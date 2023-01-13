package org.brick;

import org.brick.core.FlowDoc;
import org.brick.core.IPureFunction;

public class PureProc1 implements IPureFunction<String, Integer, Integer> {

    private String desc;

    public PureProc1(String desc) {
        this.desc = desc;
    }

    @Override
    public Integer pureCalculate(String input, Integer context) {
        System.out.println(String.format("Proc1 input: %s, context: %d", input, context));
        System.out.println(String.format("Proc1 output: %s", context + 1));
        return context + 1;
    }

    @Override
    public FlowDoc<String, Integer, Integer> getFlowDoc() {
        FlowDoc<String,Integer,Integer> flowDoc = new FlowDoc<>(this.desc);
        return flowDoc.types(String.class,Integer.class, Integer.class);
    }

    @Override
    public String getFlowType() {
        return "IPureProcess";
    }
}
