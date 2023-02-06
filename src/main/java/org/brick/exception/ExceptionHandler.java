package org.brick.exception;

public interface ExceptionHandler<O> {

    O handler(Object content);
}
