package org.brick;

public interface IAsyncFlow<I,O,C> extends IFlow<I,O,C>{

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
}
