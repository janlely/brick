package org.brick.model;

import org.apache.commons.lang3.ClassUtils;
import org.brick.core.ISideEffect;

public interface IModifyCacheFlow<I,O,C> extends ISideEffect<I,O,C> {

    String getKey();

    String getType();

    O doCacheModify(final I input, C context);

    default O processWithSideEffect(final I input, C context) {
        return doCacheModify(input, context);
    }

    @Override
    default String getFlowName() {
        return ISideEffect.super.getFlowName() + ":" + ClassUtils.getShortClassName(IModifyCacheFlow.class);
    }
}
