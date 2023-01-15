package org.brick.core;

import org.apache.commons.lang3.ClassUtils;

import java.io.Serializable;

public interface IAsyncFlow<I extends Serializable,O,C> extends SubFlow.ISubFlow<I,O,C> {

    void async(I input, C context);

    @Override
    default O run(final I input, C context) {
        async(input, context);
        return null;
    }

    @Override
    default boolean isAsync() {
        return true;
    }


    @Override
    default String getFlowType() {
        return SubFlow.ISubFlow.super.getFlowType() + ":" + ClassUtils.getShortClassName(IAsyncFlow.class);
    }
}
