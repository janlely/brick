package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiConsumer;

/**
 * @param <I> the input type
 * @param <C> the context type
 */
public class ModifyContext<I,C> implements Flow<I,I,C>{

    /**
     * the modifier
     */
    private BiConsumer<I,C> modifier;
    /**
     * the description
     */
    private String desc;

    /**
     * @param desc the description
     * @param modifier the modifier
     */
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

    /**
     * @param input the input
     * @param context the context
     * @return
     */
    public C mod(I input, C context) {
        modify(input, context);
        return context;
    }

    /**
     * @param input the input
     * @param context the context
     */
    public void modify(I input, C context) {
        this.modifier.accept(input, context);
    }
}
