package org.brick.core;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.ClassUtils;

import java.io.Serializable;
import java.util.function.BiConsumer;

public class AsyncFlow<I extends Serializable,O,C> implements IAsyncFlow<I,O,C> {

    private String desc;
    private BiConsumer<I,C> proc;

    public AsyncFlow(String desc, BiConsumer<I,C> proc) {
        this.desc = desc;
        this.proc = proc;
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, getFlowType());
        Class<?>[] classes = TypeResolver.resolveRawArguments(AsyncFlow.class, this.getClass());
        return flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
    }

    @Override
    public String getFlowType() {
        return IAsyncFlow.super.getFlowType() + ":" + ClassUtils.getShortClassName(AsyncFlow.class);
    }

    @Override
    public void async(final I input, C context) {
        this.proc.accept(input, context);
    }
}
