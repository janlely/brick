package org.brick.core;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiConsumer;

public class ModifyInputFlow<I,C> implements ISideEffect<I,I,C> {

    private String desc;
    private BiConsumer<I,C> func;

    public ModifyInputFlow(String desc, BiConsumer<I,C> func) {
        this.desc = desc;
        this.func = func;
    }

    @Override
    public FlowDoc<I, I, C> getFlowDoc() {
        FlowDoc<I,I,C> flowDoc = new FlowDoc<>(this.desc, getFlowType());
        Class<?>[] classes = TypeResolver.resolveRawArguments(ModifyInputFlow.class, this.getClass());
        return flowDoc.types((Class<I>) classes[0], (Class<I>) classes[0], (Class<C>) classes[1]);
    }

    @Override
    public String getFlowType() {
        return ISideEffect.super.getFlowType() + ":" + ClassUtils.getShortClassName(ModifyInputFlow.class);
    }

    @Override
    public I processWithSideEffect(final I input, C context) {
        this.func.accept(input, context);
        return input;
    }
}
