package org.brick.core;

import org.apache.commons.lang3.ClassUtils;

public class CaseFlow<I,O,C,P> implements SubFlow.ISubFlow<I,O,C> {

    private P value;
    private SubFlow.ISubFlow<I,O,C> flow;

    public CaseFlow(P value, Flow<I,O,C> flow) {
        assert SubFlow.ISubFlow.class.isAssignableFrom(flow.getClass());
        this.value = value;
        this.flow = (SubFlow.ISubFlow<I, O, C>) flow;
    }

    public P getValue() {
        return this.value;
    }

    public SubFlow.ISubFlow<I,O,C> getFlow() {
        return this.flow;
    }

    @Override
    public O run(I input, C context) {
        return this.flow.run(input, context);
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        return this.flow.getFlowDoc();
    }

    @Override
    public String getFlowType() {
        return SubFlow.ISubFlow.super.getFlowType() + ":" + ClassUtils.getShortClassName(CaseFlow.class);
    }
}
