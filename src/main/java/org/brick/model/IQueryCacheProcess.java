package org.brick.model;

import org.brick.core.ISideEffect;

public interface IQueryCacheProcess<I,O,C> extends ISideEffect<I,O,C> {

    O doQueryCache(I input, C context);

    default O processWithSideEffect(I input, C context) {
        return doQueryCache(input, context);
    }

}
