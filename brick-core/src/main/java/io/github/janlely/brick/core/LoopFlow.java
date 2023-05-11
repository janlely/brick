package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiFunction;

/**
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context
 */
public class LoopFlow<I,O,C> implements Flow<I,O,C>{

    /**
     * the inner flow
     */
    private Flow<I,O,C> innerFlow;
    /**
     * the loop continue condition
     */
    private BiFunction<I,C,Boolean> loopCond;
    /**
     * the input updater
     */
    private InputUpdater<I,O,C> inputUpdater;
    /**
     * the default value if loop can't execute
     */
    private BiFunction<I,C,O> defaultValue;
    /**
     * the description
     */
    private String desc;

    /**
     * @param desc the description
     * @param defaultValue the default value
     * @param loopCond the loop condition
     * @param inputUpdater the input updater
     * @param innerFlow
     */
    public LoopFlow(String desc, BiFunction<I,C,O> defaultValue, BiFunction<I,C,Boolean> loopCond,
                    InputUpdater<I,O,C> inputUpdater, Flow<I,O,C> innerFlow) {
        assert SubFlow.ISubFlow.class.isAssignableFrom(innerFlow.getClass());
        this.innerFlow = innerFlow;
        this.defaultValue = defaultValue;
        this.loopCond = loopCond;
        this.inputUpdater = inputUpdater;
        this.desc = desc;
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I, O, C> flowDoc = new FlowDoc<>(this.desc, FlowType.LOOP, this.getFlowName());
        flowDoc.add(innerFlow.getFlowDoc());
        return flowDoc;
    }

    @Override
    public String getFlowName() {
        return ClassUtils.getShortClassName(LoopFlow.class);
    }

    @Override
    public O run(I input, C context) {
        I i = input;
        O o = this.defaultValue.apply(input, context);
        while(this.loopCond.apply(i, context)) {
            o = this.innerFlow.run(i, context);
            i = this.inputUpdater.update(i,o, context);
        }
        return o;
    }

    /**
     * @param <I> the input type
     * @param <O> the output type
     * @param <C> the context type
     */
    public interface InputUpdater<I,O,C> {
        /**
         * @param input the input
         * @param output the output
         * @param context the context
         * @return the new input
         */
        I update(I input, O output, C context);
    }
}
