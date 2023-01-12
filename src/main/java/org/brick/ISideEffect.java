package org.brick;

public interface ISideEffect<I,O,C> {
    O exec(I input, C context);
}
