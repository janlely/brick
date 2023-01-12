package org.brick.core;

public interface IPureProcess<I,O,C> extends IFlow<I, O, C> {

    O pureCalculate(I input, C context);

    default O run(I input, C context) {
        return pureCalculate(input, context);
    }
}
