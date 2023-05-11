package io.github.janlely.brick.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * the doc of the flow
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context
 */
public class FlowDoc<I,O,C> {

    /**
     * the description
     */
    @JsonSerialize(using = StringSerializer.class)
    protected String desc;
    /**
     * the type of flow
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected FlowType flowType;
    /**
     * the name of flow
     */
    @JsonSerialize(using = StringSerializer.class)
    protected String flowName;

    /**
     * the doc of the inner flows
     */
    @JsonManagedReference
    List<FlowDoc> innerFlowDocs;

    /**
     * the constructor
     */
    public FlowDoc() {
        this.innerFlowDocs = new ArrayList<>();
    }

    /**
     * @param desc the description
     * @param flowType the type of flow
     * @param flowName the name of flow
     */
    public FlowDoc(String desc, FlowType flowType, String flowName) {
        this.desc = desc;
        this.flowType = flowType;
        this.flowName = flowName;
        this.innerFlowDocs = new ArrayList<>();
    }

    /**
     * @param doc add a child flow doc
     */
    public void add(FlowDoc doc) {
        this.innerFlowDocs.add(doc);
    }

    /**
     * @param flowName the name of flow
     * @return this
     */
    protected FlowDoc<I,O,C> setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }


}
