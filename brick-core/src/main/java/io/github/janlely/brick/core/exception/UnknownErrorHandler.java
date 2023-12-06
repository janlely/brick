package io.github.janlely.brick.core.exception;

public interface UnknownErrorHandler<O,C> {

    O handler(Exception e, C context);
}
