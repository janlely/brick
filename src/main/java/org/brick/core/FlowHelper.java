package org.brick.core;

import net.jodah.typetools.TypeResolver;

import java.io.Serializable;

public class FlowHelper {

    public static <I,O,C> SubFlow.ISubFlow<I,O,C> fromPure(IPureFunction<I,O,C> func) {
        return new SubFlow.ISubFlow<>() {
            @Override
            public FlowDoc<I, O, C> getFlowDoc() {
                Class<?>[] classes = TypeResolver.resolveRawArguments(IPureFunction.class, func.getClass());
                return new FlowDoc<I,O,C>(func.getFlowDoc().desc).types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
            }

            @Override
            public String getFlowType() {
                return func.getFlowType();
            }

            @Override
            public O run(I input, C context) {
                return func.pureCalculate(input, context);
            }
        };
    }

    public static <I extends Serializable,O,C> SubFlow.ISubFlow<I,O,C> fromAsync(IAsyncFlow<I,O,C> flow) {
        return new SubFlow.ISubFlow<>() {
            @Override
            public FlowDoc<I, O, C> getFlowDoc() {
                Class<?>[] classes = TypeResolver.resolveRawArguments(IAsyncFlow.class, flow.getClass());
                return new FlowDoc<I,O,C>(flow.getFlowDoc().desc).types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
            }

            @Override
            public O run(I input, C context) {
                flow.run(input, context);
                return null;
            }
        };
    }


}
