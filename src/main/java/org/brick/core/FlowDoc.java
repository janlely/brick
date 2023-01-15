package org.brick.core;

import java.util.ArrayList;
import java.util.List;

public class FlowDoc<I,O,C> {

    protected Class<I> inputClass;
    protected Class<O> outputClass;
    protected Class<C> contextClass;
    protected String desc;
    protected String flowType;

    List<FlowDoc> innerFlowDocs;

    public FlowDoc() {
        this.innerFlowDocs = new ArrayList<>();
    }

    public FlowDoc(String desc, String flowType) {
        this.desc = desc;
        this.flowType = flowType;
    }

    public void add(FlowDoc doc) {
        this.innerFlowDocs.add(doc);
    }

    protected FlowDoc<I,O,C> setFlowType(String flowType) {
        this.flowType = flowType;
        return this;
    }

    public FlowDoc<I,O,C> types(Class<I> inputClass, Class<O> outputClass, Class<C> contextClass) {
        this.inputClass = inputClass;
        this.outputClass = outputClass;
        this.contextClass = contextClass;
        return this;
    }

}
