package org.brick.core;

import org.brick.core.ISideEffect;

public interface IModifyCacheFlow<I,O,C> extends ISideEffect<I,O,C> {

    String getKey();

    String getType();

    O doCacheModify(final I input, C context);

    default O processWithSideEffect(final I input, C context) {
        return doCacheModify(input, context);
    }

}
