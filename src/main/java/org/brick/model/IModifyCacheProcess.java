package org.brick.model;

import org.brick.core.ISideEffect;

public interface IModifyCacheProcess<I,O,C> extends ISideEffect<I,O,C> {

    String getKey();

    String getType();

    O doCacheModify(I input, C context);

    default O processWithSideEffect(I input, C context) {
        return doCacheModify(input, context);
    }

}
