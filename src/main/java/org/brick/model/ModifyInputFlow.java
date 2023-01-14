package org.brick.model;

import net.jodah.typetools.TypeResolver;
import org.brick.core.FlowDoc;
import org.brick.core.ISideEffect;

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
        FlowDoc<I,I,C> flowDoc = new FlowDoc<>(this.desc);
        Class<?>[] classes = TypeResolver.resolveRawArguments(ModifyInputFlow.class, this.getClass());
        return flowDoc.types((Class<I>) classes[0], (Class<I>) classes[1], (Class<C>) classes[2]);
    }

    @Override
    public String getFlowType() {
        return "ModifyInputFlow";
    }

    @Override
    public I processWithSideEffect(final I input, C context) {
        this.func.accept(input, context);
        return input;
    }
}
