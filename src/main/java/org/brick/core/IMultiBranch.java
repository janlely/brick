package org.brick.core;

public interface IMultiBranch<I,O,C,P> extends Flow<I,O,C> {

    P pattern(I input);

    Flow<I,O,C> select(P value);

    default O run(I input, C context) {
        return select(pattern(input)).run(input, context);
    }
}
