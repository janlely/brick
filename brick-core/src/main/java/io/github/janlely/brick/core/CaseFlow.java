package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

/**
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context
 * @param <P> the patter type
 */
public class CaseFlow<I,O,C,P> implements SubFlow.ISubFlow<I,O,C> {

    /**
     * the pattern value
     */
    private P value;
    /**
     * the case flow
     */
    private SubFlow.ISubFlow<I,O,C> flow;

    /**
     * @param value the pattern value
     * @param flow the case flow
     */
    public CaseFlow(P value, Flow<I,O,C> flow) {
        assert SubFlow.ISubFlow.class.isAssignableFrom(flow.getClass());
        this.value = value;
        this.flow = (SubFlow.ISubFlow<I, O, C>) flow;
    }

    /**
     * @return get the pattern value
     */
    public P getValue() {
        return this.value;
    }

    /**
     * @return get the case flow
     */
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
    public String getFlowName() {
        return SubFlow.ISubFlow.super.getFlowName() + ":" + ClassUtils.getShortClassName(CaseFlow.class);
    }
}
