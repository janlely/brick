package org.brick.core;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiFunction;

public class PureFunction<I,O,C> implements IPureFunction<I,O,C> {

    private String desc;

    private BiFunction<I,C,O> func;

    public PureFunction(String desc, BiFunction<I,C,O> func) {
        this.desc = desc;
        this.func = func;
    }

    @Override
    public String getFlowName() {
        return IPureFunction.super.getFlowName() + ":" + ClassUtils.getShortClassName(PureFunction.class);
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, FlowType.PURE_FUNCTION, getFlowName());
//        Class<?>[] classes = TypeResolver.resolveRawArguments(PureFunction.class, this.getClass());
//        return flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
        return flowDoc;
    }

    @Override
    public O pureCalculate(final I input, C context) {
        return this.func.apply(input,context);
    }
}
