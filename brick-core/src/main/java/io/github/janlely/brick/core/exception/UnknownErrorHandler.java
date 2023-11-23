package io.github.janlely.brick.core.exception;

public interface UnknownErrorHandler<O> {

    O handler(Exception e);
}
