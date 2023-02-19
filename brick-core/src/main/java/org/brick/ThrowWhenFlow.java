package org.brick;

import org.apache.commons.lang3.ClassUtils;
import org.brick.exception.FlowException;

import java.util.function.BiFunction;

public class ThrowWhenFlow<I,C> implements Flow<I,I,C>{

    private BiFunction<I,C,Boolean> cond;
    private BiFunction<I,C,FlowException> exception;

    private String desc;

    public ThrowWhenFlow(String desc, BiFunction<I,C,Boolean> cond,
                         BiFunction<I,C,FlowException> exception) {
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
