package org.brick.core;

import net.jodah.typetools.TypeResolver;

import java.io.Serializable;

public class FlowHelper {

    public static <I,O,C> SubFlow.ISubFlow<I,O,C> fromPure(PureFunction<I,O,C> func) {
        return new SubFlow.ISubFlow<>() {
            @Override
            public FlowDoc<I, O, C> getFlowDoc() {
                return func.getFlowDoc().setFlowType(getFlowType());
            }

            @Override
            public O run(I input, C context) {
                return func.pureCalculate(input, context);
            }

            @Override
            public String getFlowType() {
                return SubFlow.ISubFlow.super.getFlowType() + ":" + func.getFlowType();
            }
        };
    }

    public static <I,O,C> SubFlow.ISubFlow<I,O,C> fromEffect(SideEffect<I,O,C> func) {
        return new SubFlow.ISubFlow<>() {
            @Override
            public FlowDoc<I, O, C> getFlowDoc() {
                return func.getFlowDoc().setFlowType(getFlowType());
            }

            @Override
            public O run(I input, C context) {
                return func.processWithSideEffect(input, context);
            }

            @Override
            public String getFlowType() {
                return SubFlow.ISubFlow.super.getFlowType() + ":" + func.getFlowType();
            }
        };
    }


}
