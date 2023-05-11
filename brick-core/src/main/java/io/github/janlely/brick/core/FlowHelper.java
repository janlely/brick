package io.github.janlely.brick.core;

/**
 * quickly make a ISubFlow
 */
public class FlowHelper {

    /**
     * make ISubFlow from pure function
     * @param func the pure function
     * @param <I> the input type
     * @param <O> the output type
     * @param <C> the context
     * @return the ISubFlow
     */
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

    /**
     * make ISubFlow from effect
     * @param func the effect function
     * @param <I> the input type
     * @param <O> the output type
     * @param <C> the context
     * @return the ISubFlow
     */
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


    /**
     * make ISubFlow from a const value
     * @param value the value
     * @param <I> the input type
     * @param <O> the output type
     * @param <C> the context
     * @return the ISubFlow
     */
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
