package org.brick.exception;

import lombok.Data;

@Data
public class FlowException extends RuntimeException{

    int type;
    Object content;

    public FlowException(int type, Object content, Exception e) {
        super(e);
        this.type = type;
        this.content = content;
    }
}
