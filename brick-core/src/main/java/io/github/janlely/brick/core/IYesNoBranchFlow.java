package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiFunction;

/**
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context type
 */
public interface IYesNoBranchFlow<I,O,C> extends SubFlow.ISubFlow<I, O, C> {
    /**
     * @return is yes
     */
    BiFunction<I,C,Boolean> isYes();

    /**
     * @return the yes flow
     */
    SubFlow.ISubFlow<I,O,C> yes();

    /**
     * @return the no flow
     */
    SubFlow.ISubFlow<I,O,C> no();

    default O run(final I input, C context) {
        if (isYes().apply(input, context)) {
            return yes().run(input, context);
        }
        return no().run(input, context);
    }

    @Override
    default String getFlowName() {
        return SubFlow.ISubFlow.super.getFlowName() + ":" + ClassUtils.getShortClassName(IYesNoBranchFlow.class);
    }
}
