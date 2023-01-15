package org.brick.model;

import org.apache.commons.lang3.ClassUtils;

public interface IModifyCachePureFlow<I,C> extends IModifyCacheFlow<I,I,C> {

    void doCacheModifyPure(final I input, C context);

    default I doCacheModify(final I input, C context) {
        doCacheModifyPure(input, context);
        return input;
    }

    @Override
    default String getFlowType() {
        return IModifyCacheFlow.super.getFlowType() + ":" + ClassUtils.getShortClassName(IModifyCachePureFlow.class);
    }
}
