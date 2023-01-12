package org.brick;

public interface Flow<I,O> {
    O run(I input);
}
