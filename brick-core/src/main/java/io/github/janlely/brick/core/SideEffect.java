package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

/**
 * the side effect
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context type
 */
public class SideEffect<I,O,C> implements ISideEffect<I,O,C>{

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
    public SideEffect(String desc, UnitFunction<I,O,C> unit) {
        this.desc = desc;
        this.unit = unit;
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, FlowType.EFFECT, this.getFlowName());
        return flowDoc;
    }

    @Override
    public O processWithSideEffect(I input, C context) {
        return this.unit.exec(input,context);
    }

    @Override
    public String getFlowName() {
        return ISideEffect.super.getFlowName() + ":" + ClassUtils.getShortClassName(SideEffect.class);
    }
}
