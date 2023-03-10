package org.brick;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;

import java.util.ArrayList;
import java.util.List;

public class FlowDoc<I,O,C> {

//    @JsonSerialize(using = ClassSerializer.class)
//    protected Class<I> inputClass;
//    @JsonSerialize(using = ClassSerializer.class)
//    protected Class<O> outputClass;
//    @JsonSerialize(using = ClassSerializer.class)
//    protected Class<C> contextClass;
    @JsonSerialize(using = StringSerializer.class)
    protected String desc;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected FlowType flowType;
    @JsonSerialize(using = StringSerializer.class)
    protected String flowName;

    @JsonManagedReference
    List<FlowDoc> innerFlowDocs;

    public FlowDoc() {
        this.innerFlowDocs = new ArrayList<>();
    }

    public FlowDoc(String desc, FlowType flowType, String flowName) {
        this.desc = desc;
        this.flowType = flowType;
        this.flowName = flowName;
        this.innerFlowDocs = new ArrayList<>();
    }

    public void add(FlowDoc doc) {
        this.innerFlowDocs.add(doc);
    }

    protected FlowDoc<I,O,C> setFlowType(FlowType flowType) {
        this.flowType = flowType;
        return this;
    }

    protected FlowDoc<I,O,C> setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

//    public FlowDoc<I,O,C> types(Class<I> inputClass, Class<O> outputClass, Class<C> contextClass) {
//        this.inputClass = inputClass;
//        this.outputClass = outputClass;
//        this.contextClass = contextClass;
//        return this;
//    }

}
