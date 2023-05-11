package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

/**
 * the flow just for debug
 * @param <I> the input type
 * @param <C> the context type
 */
public interface TraceFlow<I,C> extends Flow<I,I,C>{

    @Override
    default FlowDoc<I, I, C> getFlowDoc() {
        return new FlowDoc<>("trace", FlowType.TRACE_FLOW, this.getFlowName());
    }

    @Override
    default String getFlowName() {
        return ClassUtils.getShortClassName(TraceFlow.class);
    }

    /**
     * @param input the input
     * @param context the context
     */
    void trace(I input, C context);

    default I run(final I input, C context) {
        trace(input, context);
        return input;
    }
}
