package org.brick.core;

public interface IYesNoBranchFlow<I,O,C> extends Flow<I, O, C> {
    IPureProcess<I,Boolean,C> isYes();
    Flow<I,O,C> yes();
    Flow<I,O,C> no();

    default O run(I input, C context) {
        if (isYes().run(input, context)) {
            return yes().run(input, context);
        }
        return no().run(input, context);
    }
}
