package org.brick.model;

import net.jodah.typetools.TypeResolver;
import org.brick.core.FlowDoc;
import org.brick.core.IAsyncFlow;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class AsyncFlow<I extends Serializable,O,C> implements IAsyncFlow<I,O,C> {

    private String desc;
    private BiConsumer<I,C> proc;

    public AsyncFlow(String desc, BiConsumer<I,C> proc) {
        this.desc = desc;
        this.proc = proc;
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc);
        Class<?>[] classes = TypeResolver.resolveRawArguments(AsyncFlow.class, this.getClass());
        return flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
    }

    @Override
    public void async(final I input, C context) {
        this.proc.accept(input, context);
    }
}
