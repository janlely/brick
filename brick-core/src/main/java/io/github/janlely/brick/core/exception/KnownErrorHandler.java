package io.github.janlely.brick.core.exception;

/**
 * @param <O> the output type
 */
public interface KnownErrorHandler<O,C> {

    /**
     * @param content the error content
     * @return the output value
     */
    O handler(Object content, C context);
}
