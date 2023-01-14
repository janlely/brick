package org.brick.core;

import net.jodah.typetools.TypeResolver;

public class PhantomFlow<I,C> implements Flow<I,I,C> {

    @Override
    public FlowDoc<I, I, C> getFlowDoc() {
        Class<?>[] classes = TypeResolver.resolveRawArguments(PhantomFlow.class, this.getClass());
        return new FlowDoc<I,I,C>("The End").types((Class<I>) classes[0], (Class<I>) classes[0], (Class<C>) classes[1]);
    }

    @Override
    public String getFlowType() {
        return "EndFlow";
    }

    @Override
    public I run(I input, C context) {
        return input;
    }
}
