package org.brick;

public class FlowHelper {

    public static <I,O,C> SubFlow.ISubFlow<I,O,C> fromPure(PureFunction<I,O,C> func) {
        return new SubFlow.ISubFlow<>() {
            @Override
            public FlowDoc<I, O, C> getFlowDoc() {
                return func.getFlowDoc().setFlowName(getFlowName());
            }

            @Override
            public O run(I input, C context) {
                return func.pureCalculate(input, context);
            }

            @Override
            public String getFlowName() {
                return SubFlow.ISubFlow.super.getFlowName() + ":" + func.getFlowName();
            }
        };
    }

    public static <I,O,C> SubFlow.ISubFlow<I,O,C> fromEffect(SideEffect<I,O,C> func) {
        return new SubFlow.ISubFlow<>() {
            @Override
            public FlowDoc<I, O, C> getFlowDoc() {
                return func.getFlowDoc().setFlowName(getFlowName());
            }

            @Override
            public O run(I input, C context) {
                return func.processWithSideEffect(input, context);
            }

            @Override
            public String getFlowName() {
                return SubFlow.ISubFlow.super.getFlowName() + ":" + func.getFlowName();
            }
        };
    }


    public static <I,O,C> SubFlow.ISubFlow<I,O,C> fromConst(O value) {
        return new SubFlow.ISubFlow<>() {
            @Override
            public FlowDoc<I, O, C> getFlowDoc() {
                return new FlowDoc<>("ConstFlow", FlowType.CONST_FLOW, "ConstFLOW");
            }

            @Override
            public O run(I input, C context) {
                return value;
            }

            @Override
            public String getFlowName() {
                return SubFlow.ISubFlow.super.getFlowName() + ":ConstFlow";
            }
        };
    }
}
