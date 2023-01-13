package org.brick;

import org.apache.commons.lang3.StringUtils;
import org.brick.core.FlowDoc;
import org.brick.core.IPureFunction;

public class PureProc3 implements IPureFunction<String, String, Integer> {

    private String desc;

    public PureProc3(String desc) {
        this.desc = desc;
    }

    @Override
    public String pureCalculate(String input, Integer context) {
        System.out.println(String.format("Proc3 input: %s, context: %d", input, context));
        System.out.println(String.format("Proc3 output: ", StringUtils.reverse(input)));
        return StringUtils.reverse(input);
    }

    @Override
    public FlowDoc<String, String, Integer> getFlowDoc() {
        FlowDoc<String,String,Integer> flowDoc = new FlowDoc<>(this.desc);
        return flowDoc.types(String.class,String.class, Integer.class);
    }
}
