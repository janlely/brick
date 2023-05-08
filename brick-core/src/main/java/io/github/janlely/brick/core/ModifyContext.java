package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiConsumer;

public class ModifyContext<I,C> implements Flow<I,I,C>{

    private BiConsumer<I,C> modifier;
    private String desc;

    public ModifyContext(String desc, BiConsumer<I,C> modifier) {
        this.desc = desc;
        this.modifier = modifier;
    }

    @Override
    public FlowDoc<I, I, C> getFlowDoc() {
        return new FlowDoc<>(desc, FlowType.CONTEXT_MODIFY, getFlowName());
    }

    @Override
    public String getFlowName() {
        return ClassUtils.getShortClassName(ModifyContext.class);
    }

    @Override
    public I run(I input, C context) {
        return input;
    }

    public C mod(I input, C context) {
        modify(input, context);
        return context;
    }

    public void modify(I input, C context) {
        this.modifier.accept(input, context);
    }
}
