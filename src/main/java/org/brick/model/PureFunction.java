package org.brick.model;

import net.jodah.typetools.TypeResolver;
import org.brick.core.FlowDoc;
import org.brick.core.IPureFunction;

import java.util.function.BiFunction;

public class PureFunction<I,O,C> implements IPureFunction<I,O,C> {

    private String desc;

    private BiFunction<I,C,O> func;

    public PureFunction(String desc, BiFunction<I,C,O> func) {
        this.desc = desc;
        this.func = func;
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc);
        Class<?>[] classes = TypeResolver.resolveRawArguments(PureFunction.class, this.getClass());
        return flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
    }

    @Override
    public O pureCalculate(I input, C context) {
        return this.func.apply(input,context);
    }
}
