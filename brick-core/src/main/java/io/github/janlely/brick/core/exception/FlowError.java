package io.github.janlely.brick.core.exception;

import lombok.Data;

/**
 * the flow error class
 */
@Data
public class FlowError extends RuntimeException{

    /**
     * the type of error
     */
    int type;
    /**
     * the error content
     */
    Object content;

    /**
     * @param type the type of error
     * @param content the content of error
     */
    public FlowError(int type, Object content) {
        super();
        this.type = type;
        this.content = content;
    }

    /**
     * @param type the type of error
     * @param content the type of content
     * @param e the inner exception
     */
    public FlowError(int type, Object content, Exception e) {
        super(e);
        this.type = type;
        this.content = content;
    }
}
