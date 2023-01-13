package org.brick.core;

import java.io.Serializable;

public interface IAsyncFlow<I extends Serializable,O,C> extends Flow<I,O,C> {

    void async(I input, C context);

    @Override
    default O run(I input, C context) {
        async(input, context);
        return null;
    }

    @Override
    default boolean isAsync() {
        return true;
    }


    @Override
    default String getFlowType() {
        return "IAsyncFlow";
    }
}
