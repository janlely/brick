package io.github.janlely.brick.core.exception;

public interface ErrorHandler<O> {

    O handler(Object content);
}
