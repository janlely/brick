package org.brick.model;

import org.brick.core.ISideEffect;

public interface IModifyDBProcess<I,O,C> extends ISideEffect<I,O,C> {

    String getPseudoSql();

    O doDBModify(I input, C context);


    default O processWithSideEffect(I input, C context) {
        return doDBModify(input, context);
    }
}
