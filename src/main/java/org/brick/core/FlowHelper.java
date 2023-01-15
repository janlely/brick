package org.brick.core;

import net.jodah.typetools.TypeResolver;

import java.io.Serializable;

public class FlowHelper {

    public static <I,O,C> SubFlow.ISubFlow<I,O,C> fromPure(IPureFunction<I,O,C> func) {
        return new SubFlow.ISubFlow<>() {
            @Override
            public FlowDoc<I, O, C> getFlowDoc() {
                Class<?>[] classes = TypeResolver.resolveRawArguments(IPureFunction.class, func.getClass());
                return new FlowDoc<I,O,C>(func.getFlowDoc().desc, getFlowType()).types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
            }

            @Override
            public O run(I input, C context) {
                return func.pureCalculate(input, context);
            }
        };
    }


}
