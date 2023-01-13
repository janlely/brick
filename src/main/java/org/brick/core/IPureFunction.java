package org.brick.core;

public interface IPureFunction<I,O,C> extends Flow<I, O, C> {

    O pureCalculate(I input, C context);

    default O run(I input, C context) {
        return pureCalculate(input, context);
    }

    default String getFlowType() {
        return "IPureProcess";
    }

}
