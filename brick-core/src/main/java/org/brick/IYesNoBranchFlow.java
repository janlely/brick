package org.brick;

import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiFunction;

public interface IYesNoBranchFlow<I,O,C> extends SubFlow.ISubFlow<I, O, C> {
    BiFunction<I,C,Boolean> isYes();
    SubFlow.ISubFlow<I,O,C> yes();
    SubFlow.ISubFlow<I,O,C> no();

    default O run(final I input, C context) {
        if (isYes().apply(input, context)) {
            return yes().run(input, context);
        }
        return no().run(input, context);
    }

    @Override
    default String getFlowName() {
        return SubFlow.ISubFlow.super.getFlowName() + ":" + ClassUtils.getShortClassName(IYesNoBranchFlow.class);
    }
}
