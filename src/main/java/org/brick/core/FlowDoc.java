package org.brick.core;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ClassSerializer;

import java.util.ArrayList;
import java.util.List;

public class FlowDoc<I,O,C> {

    @JsonSerialize(using = ClassSerializer.class)
    protected Class<I> inputClass;
    @JsonSerialize(using = ClassSerializer.class)
    protected Class<O> outputClass;
    @JsonSerialize(using = ClassSerializer.class)
    protected Class<C> contextClass;
    protected String desc;
    protected String flowType;

    @JsonManagedReference
    List<FlowDoc> innerFlowDocs;

    public FlowDoc() {
        this.innerFlowDocs = new ArrayList<>();
    }

    public FlowDoc(String desc, String flowType) {
        this.desc = desc;
        this.flowType = flowType;
        this.innerFlowDocs = new ArrayList<>();
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
