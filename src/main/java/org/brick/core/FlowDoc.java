package org.brick.core;

import java.util.ArrayList;
import java.util.List;

public class FlowDoc<I,O,C> {

    protected Class<I> inputClass;
    protected Class<O> outputClass;
    protected Class<C> contextClass;
    protected String desc;

    List<FlowDoc> innerFlowDocs;

    public FlowDoc() {
        this.innerFlowDocs = new ArrayList<>();
    }

    public FlowDoc(String desc) {
        this.desc = desc;
    }

    public void add(FlowDoc doc) {
        this.innerFlowDocs.add(doc);
    }

    public FlowDoc<I,O,C> types(Class<I> inputClass, Class<O> outputClass, Class<C> contextClass) {
        this.inputClass = inputClass;
        this.outputClass = outputClass;
        this.contextClass = contextClass;
        return this;
    }

}
