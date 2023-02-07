package org.brick;

public interface UnitFunction<I,O,C> {

    O exec(I input, C context);
}
