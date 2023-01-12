package org.brick;

public interface YesNoBranchProcess<I,O,C> {
    boolean isYes(I input, C context);
    Flow<I,O> yes(C context);
    Flow<I,O> no(C context);
}
