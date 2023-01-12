package org.brick;

public interface ISideEffect<I,O,C> extends IFlow<I,O,C> {

    O processWithSideEffect(I input, C context);

    default O run(I input, C context) {
        return processWithSideEffect(input, context);
    }
}
