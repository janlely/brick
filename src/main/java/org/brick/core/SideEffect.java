package org.brick.core;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiFunction;

public class SideEffect<I,O,C> implements ISideEffect<I,O,C>{

    private String desc;
    private BiFunction<I,C,O> func;

    public SideEffect(String desc, BiFunction<I,C,O> func) {
        this.desc = desc;
        this.func = func;
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, getFlowType());
        Class<?>[] classes = TypeResolver.resolveRawArguments(SideEffect.class, this.getClass());
        return flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
    }

    @Override
    public O processWithSideEffect(I input, C context) {
        return func.apply(input,context);
    }

    @Override
    public String getFlowType() {
        return ISideEffect.super.getFlowType() + ":" + ClassUtils.getShortClassName(SideEffect.class);
    }
}
