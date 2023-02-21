package org.brick.exception;

public interface ErrorHandler<O> {

    O handler(Object content);
}
