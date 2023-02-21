package org.brick.exception;

import lombok.Data;

@Data
public class FlowError extends RuntimeException{

    int type;
    Object content;

    public FlowError(int type, Object content) {
        super();
        this.type = type;
        this.content = content;
    }

    public FlowError(int type, Object content, Exception e) {
        super(e);
        this.type = type;
        this.content = content;
    }
}
