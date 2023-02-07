package org.brick;

import org.apache.commons.lang3.ClassUtils;

import java.util.function.Function;

public class ModifyContext<I,C> implements Flow<I,I,C>{

    private Function<C,C> modifier;
    private String desc;

    public ModifyContext(String desc, Function<C,C> modifier) {
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

    public C modify(C context) {
        return this.modifier.apply(context);
    }
}
