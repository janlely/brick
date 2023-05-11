package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

/**
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context type
 * @param <P> the pattern type
 */
public interface IMultiBranchFlow<I,O,C,P> extends SubFlow.ISubFlow<I,O,C> {

    /**
     * @param input the input
     * @param context the context
     * @return the pattern
     */
    P pattern(I input, C context);

    /**
     * @param value the pattern
     * @return the selected flow
     */
    SubFlow.ISubFlow<I,O,C> select(P value);

    default O run(final I input, C context) {
        return select(pattern(input, context)).run(input, context);
    }

    @Override
    default String getFlowName() {
        return SubFlow.ISubFlow.super.getFlowName() + ":" + ClassUtils.getShortClassName(IMultiBranchFlow.class);
    }
}
