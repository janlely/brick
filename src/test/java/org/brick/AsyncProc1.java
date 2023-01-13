package org.brick;

import org.brick.core.FlowDoc;
import org.brick.core.IAsyncFlow;

public class AsyncProc1 implements IAsyncFlow<String, String, Integer> {

    private String desc;

    public AsyncProc1(String desc) {
        this.desc = desc;
    }

    @Override
    public void async(String input, Integer context) {
        System.out.println("Async input: " + input);
    }

    @Override
    public FlowDoc<String, String, Integer> getFlowDoc() {
        FlowDoc<String,String,Integer> flowDoc = new FlowDoc<>(this.desc);
        return flowDoc.types(String.class, String.class, Integer.class);
    }

}
