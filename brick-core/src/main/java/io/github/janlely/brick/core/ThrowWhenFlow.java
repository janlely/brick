package io.github.janlely.brick.core;

import io.github.janlely.brick.core.exception.FlowError;
import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiFunction;

/**
 * the throw when abstraction
 * @param <I> the input type
 * @param <C> the context type
 */
public class ThrowWhenFlow<I,C> implements Flow<I,I,C>{

    /**
     * condition to throw
     */
    private BiFunction<I,C,Boolean> cond;
    /**
     * exception to throw out
     */
    private BiFunction<I,C, FlowError> exception;

    /**
     * the description
     */
    private String desc;

    /**
     * @param desc the description
     * @param cond the condition
     * @param exception the exception
     */
    public ThrowWhenFlow(String desc, BiFunction<I,C,Boolean> cond,
                         BiFunction<I,C, FlowError> exception) {
        this.desc = desc;
        this.cond = cond;
        this.exception = exception;
    }
    @Override
    public FlowDoc<I, I, C> getFlowDoc() {
        FlowDoc<I,I,C> flowDoc = new FlowDoc<>(this.desc, FlowType.ABORT, getFlowName());
        return flowDoc;
    }

    @Override
    public String getFlowName() {
        return ClassUtils.getShortClassName(ThrowWhenFlow.class);
    }

    @Override
    public I run(I input, C context) {
        if (this.cond.apply(input, context)) {
            throw this.exception.apply(input, context);
        }
        return input;
    }
}
