package org.brick.core;

import org.brick.core.ISideEffect;

public interface IModifyDBFlow<I,O,C> extends ISideEffect<I,O,C> {

    String getPseudoSql();

    O doDBModify(final I input, C context);


    default O processWithSideEffect(final I input, C context) {
        return doDBModify(input, context);
    }
}
