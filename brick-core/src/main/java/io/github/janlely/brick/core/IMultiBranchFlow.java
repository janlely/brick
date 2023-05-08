package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

public interface IMultiBranchFlow<I,O,C,P> extends SubFlow.ISubFlow<I,O,C> {

    P pattern(I input);

    SubFlow.ISubFlow<I,O,C> select(P value);

    default O run(final I input, C context) {
        return select(pattern(input)).run(input, context);
    }

    @Override
    default String getFlowName() {
        return SubFlow.ISubFlow.super.getFlowName() + ":" + ClassUtils.getShortClassName(IMultiBranchFlow.class);
    }
}
