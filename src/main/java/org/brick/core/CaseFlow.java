package org.brick.core;

public class CaseFlow<I,O,C,P> implements Flow<I,O,C>{

    private P value;
    private Flow<I,O,C> flow;

    public CaseFlow(P value, Flow<I,O,C> flow) {
        this.value = value;
        this.flow = flow;
    }

    public P getValue() {
        return this.value;
    }

    public Flow<I,O,C> getFlow() {
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
        return "CaseFlow";
    }
}
