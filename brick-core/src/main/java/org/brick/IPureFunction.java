package org.brick;

import org.apache.commons.lang3.ClassUtils;

public interface IPureFunction<I,O,C> extends Flow<I, O, C> {

    O pureCalculate(final I input, C context);

    default O run(final I input, C context) {
        return pureCalculate(input, context);
    }

    default String getFlowName() {
        return ClassUtils.getShortClassName(IPureFunction.class);
    }

}
