package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

/**
 * the sub flow
 */
public class SubFlow {

    /**
     * @param <I> the input type
     * @param <O> the output type
     * @param <C> the context
     */
    protected interface ISubFlow<I,O,C> extends Flow<I,O,C>{

        @Override
        default String getFlowName() {
            return ClassUtils.getShortClassName(SubFlow.class);
        }
    }
}
