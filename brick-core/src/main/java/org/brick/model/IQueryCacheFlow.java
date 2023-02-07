package org.brick.model;

import org.apache.commons.lang3.ClassUtils;
import org.brick.ISideEffect;

public interface IQueryCacheFlow<I,O,C> extends ISideEffect<I,O,C> {

    O doQueryCache(final I input, C context);

    default O processWithSideEffect(final I input, C context) {
        return doQueryCache(input, context);
    }

    @Override
    default String getFlowName() {
        return ISideEffect.super.getFlowName() + ClassUtils.getShortClassName(IQueryCacheFlow.class);
    }
}
