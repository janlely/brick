package org.brick.model;

import org.brick.core.ISideEffect;

public interface IQueryDBProcess<I,O,C> extends ISideEffect<I,O,C> {

    O doDBQuery(I input, C context);

    default O processWithSideEffect(I input, C context) {
        return doDBQuery(input, context);
    }

}
