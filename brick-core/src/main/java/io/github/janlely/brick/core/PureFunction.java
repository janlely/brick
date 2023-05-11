package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

/**
 * the pure function
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the contex type
 */
public class PureFunction<I,O,C> implements IPureFunction<I,O,C> {

    /**
     * the description
     */
    private String desc;

    /**
     * compute unit
     */
    private UnitFunction<I,O,C> unit;

    /**
     * @param desc the description
     * @param unit the unit
     */
    public PureFunction(String desc, UnitFunction<I,O,C> unit) {
        this.desc = desc;
        this.unit = unit;
    }

    @Override
    public String getFlowName() {
        return IPureFunction.super.getFlowName() + ":" + ClassUtils.getShortClassName(PureFunction.class);
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, FlowType.PURE_FUNCTION, getFlowName());
        return flowDoc;
    }

    @Override
    public O pureCalculate(final I input, C context) {
        return this.unit.exec(input,context);
    }
}
