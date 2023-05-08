package io.github.janlely.brick.core;

public interface UnitFunction<I,O,C> {

    O exec(I input, C context);
}
