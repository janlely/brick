package org.brick;

public interface IYesNoBranchProcess<I,O,C> extends IProcess<I,O,C> {
    boolean isYes(I input, C context);
    IFlow<I,O> yes(C context);
    IFlow<I,O> no(C context);
}
