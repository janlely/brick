package org.brick.core;

public interface IFlow<I,O,C> {
    O run(I input, C context);
    default boolean isAsync() {
        return false;
    };
}
