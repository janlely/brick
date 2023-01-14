package org.brick.core;

import org.brick.core.ISideEffect;

public interface IQueryDBFlow<I,O,C> extends ISideEffect<I,O,C> {

    O doDBQuery(final I input, C context);

    default O processWithSideEffect(final I input, C context) {
        return doDBQuery(input, context);
    }

}
