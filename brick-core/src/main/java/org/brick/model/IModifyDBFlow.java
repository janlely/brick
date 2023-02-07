package org.brick.model;

import org.apache.commons.lang3.ClassUtils;
import org.brick.ISideEffect;

public interface IModifyDBFlow<I,O,C> extends ISideEffect<I,O,C> {

    String getPseudoSql();

    O doDBModify(final I input, C context);


    default O processWithSideEffect(final I input, C context) {
        return doDBModify(input, context);
    }

    @Override
    default String getFlowName() {
        return ISideEffect.super.getFlowName() + ":" + ClassUtils.getShortClassName(IModifyDBFlow.class);
    }
}
