package org.brick;

public interface IFlow<I,O> {
    O run(I input);
}
