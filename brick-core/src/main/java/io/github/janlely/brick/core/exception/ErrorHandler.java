package io.github.janlely.brick.core.exception;

/**
 * @param <O> the output type
 */
public interface ErrorHandler<O> {

    /**
     * @param content the error content
     * @return the output value
     */
    O handler(Object content);
}
