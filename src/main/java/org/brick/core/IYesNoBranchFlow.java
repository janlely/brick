package org.brick.core;

import java.util.function.BiFunction;

public interface IYesNoBranchFlow<I,O,C> extends Flow<I, O, C> {
    BiFunction<I,C,Boolean> isYes();
    Flow<I,O,C> yes();
    Flow<I,O,C> no();

    default O run(final I input, C context) {
        if (isYes().apply(input, context)) {
            return yes().run(input, context);
        }
        return no().run(input, context);
    }

    @Override
    default String getFlowType() {
        return "IYesNoBranchFlow";
    }
}
