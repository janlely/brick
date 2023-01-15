package org.brick.model;

import org.apache.commons.lang3.ClassUtils;

public interface IModifyDBPureFlow<I,C> extends IModifyDBFlow<I,I,C> {

    void doDBModifyPure(final I input, C context);

    default I doDBModify(final I input, C context) {
        doDBModifyPure(input, context);
        return input;
    }

    @Override
    default String getFlowType() {
        return IModifyDBFlow.super.getFlowType() + ":" + ClassUtils.getShortClassName(IModifyCachePureFlow.class);
    }
}
