package org.brick.core;

import org.apache.commons.lang3.ClassUtils;

public interface ISideEffect<I,O,C> extends Flow<I,O,C> {

    O processWithSideEffect(final I input, C context);

    default O run(final I input, C context) {
        return processWithSideEffect(input, context);
    }

    @Override
    default String getFlowType() {
        return ClassUtils.getShortClassName(ISideEffect.class);
    }

}
