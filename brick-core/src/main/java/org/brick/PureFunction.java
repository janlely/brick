package org.brick;

import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiFunction;

public class PureFunction<I,O,C> implements IPureFunction<I,O,C> {

    private String desc;

    private UnitFunction<I,O,C> unit;

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
