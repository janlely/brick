package org.brick.core;

public interface IYesNoBranchFlow<I,O,C> extends IFlow<I, O, C> {
    boolean isYes(I input, C context);
    IFlow<I,O,C> yes(C context);
    IFlow<I,O,C> no(C context);

    default O run(I input, C context) {
        if (isYes(input, context)) {
            return yes(context).run(input, context);
        }
        return no(context).run(input, context);
    }
}
