package org.brick;

public interface SideEffect<I,O,C> {
    O exec(I input, C context);
}
