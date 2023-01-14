package org.brick.model;

public interface IModifyCachePureFlow<I,C> extends IModifyCacheFlow<I,I,C> {

    void doCacheModifyPure(final I input, C context);

    default I doCacheModify(final I input, C context) {
        doCacheModifyPure(input, context);
        return input;
    }
}
