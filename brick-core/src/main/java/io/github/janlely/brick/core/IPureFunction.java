package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

/**
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context type
 */
public interface IPureFunction<I,O,C> extends Flow<I, O, C> {

    /**
     * @param input the input
     * @param context the context
     * @return
     */
    O pureCalculate(final I input, C context);

    default O run(final I input, C context) {
        return pureCalculate(input, context);
    }

    default String getFlowName() {
        return ClassUtils.getShortClassName(IPureFunction.class);
    }

}
