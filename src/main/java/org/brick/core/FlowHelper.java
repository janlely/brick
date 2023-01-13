package org.brick.core;

import net.jodah.typetools.TypeResolver;

public class FlowHelper {

    public static <I,O,C> Flow<I,O,C> fromPure(IPureFunction<I,O,C> process) {
        return new Flow<I, O, C>() {
            @Override
            public FlowDoc<I, O, C> getFlowDoc() {
                Class<?>[] classes = TypeResolver.resolveRawArguments(IPureFunction.class, process.getClass());
                return new FlowDoc<I,O,C>(process.getFlowDoc().desc).types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
            }

            @Override
            public String getFlowType() {
                return process.getFlowType();
            }

            @Override
            public O run(I input, C context) {
                return process.pureCalculate(input, context);
            }
        };
    }
}
