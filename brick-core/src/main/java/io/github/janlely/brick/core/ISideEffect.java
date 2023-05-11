package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

/**
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context
 */
public interface ISideEffect<I,O,C> extends Flow<I,O,C> {

    /**
     * @param input the input
     * @param context the context
     * @return the output
     */
    O processWithSideEffect(final I input, C context);

    default O run(final I input, C context) {
        return processWithSideEffect(input, context);
    }

    @Override
    default String getFlowName() {
        return ClassUtils.getShortClassName(ISideEffect.class);
    }

}
