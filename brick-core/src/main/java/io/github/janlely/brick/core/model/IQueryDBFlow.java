package io.github.janlely.brick.core.model;

import org.apache.commons.lang3.ClassUtils;
import io.github.janlely.brick.core.ISideEffect;

public interface IQueryDBFlow<I,O,C> extends ISideEffect<I,O,C> {

    O doDBQuery(final I input, C context);

    default O processWithSideEffect(final I input, C context) {
        return doDBQuery(input, context);
    }

    @Override
    default String getFlowName() {
        return ISideEffect.super.getFlowName() + ":" + ClassUtils.getShortClassName(IQueryDBFlow.class);
    }
}
