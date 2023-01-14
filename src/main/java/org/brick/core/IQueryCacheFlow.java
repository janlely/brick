package org.brick.core;

import org.brick.core.ISideEffect;

public interface IQueryCacheFlow<I,O,C> extends ISideEffect<I,O,C> {

    O doQueryCache(final I input, C context);

    default O processWithSideEffect(final I input, C context) {
        return doQueryCache(input, context);
    }

}
