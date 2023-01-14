package org.brick.core;

public interface ISideEffect<I,O,C> extends Flow<I,O,C> {

    O processWithSideEffect(final I input, C context);

    default O run(final I input, C context) {
        return processWithSideEffect(input, context);
    }
}
