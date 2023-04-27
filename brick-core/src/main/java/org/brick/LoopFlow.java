package org.brick;

import com.fasterxml.jackson.core.io.InputDecorator;
import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;

public class LoopFlow<I,O,C> implements Flow<I,O,C>{

    private Flow<I,O,C> innerFlow;
    private BiFunction<I,C,Boolean> loopCond;
    private InputeUpdater<I,O,C> inputeUpdater;
    private BiFunction<I,C,O> defaultValue;
    private String desc;

    public LoopFlow(String desc, BiFunction<I,C,O> defaultValue, BiFunction<I,C,Boolean> loopCond,
                    InputeUpdater<I,O,C> inputeUpdater, Flow<I,O,C> innerFlow) {
        assert SubFlow.ISubFlow.class.isAssignableFrom(innerFlow.getClass());
        this.innerFlow = innerFlow;
        this.defaultValue = defaultValue;
        this.loopCond = loopCond;
        this.inputeUpdater = inputeUpdater;
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
            i = this.inputeUpdater.update(i,o, context);
        }
        return o;
    }

    public interface InputeUpdater<I,O,C> {
        I update(I input, O output, C context);
    }
}
