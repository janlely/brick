package org.brick.model;

public interface IModifyCachePureProcess<I,C> extends IModifyCacheProcess<I,I,C> {

    void doCacheModifyPure(I input, C context);

    default I doCacheModify(I input, C context) {
        doCacheModifyPure(input, context);
        return input;
    }
}
