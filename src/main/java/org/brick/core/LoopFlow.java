package org.brick.core;

import org.apache.commons.lang3.ClassUtils;
import org.brick.types.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;

public class LoopFlow<I,O,C,O1> implements Flow<I,O,C>{

    private Flow<I,O1,C> innerFlow;
    private Function<C, Collector<O1,?,O>> collector;
    private BiFunction<I,C,Boolean> endCond;
    private String desc;

    public LoopFlow(String desc, BiFunction<I,C,Boolean> endCond ,Flow<I,O1,C> innerFlow,  Function<C, Collector<O1,?,O>> collector) {
        assert SubFlow.ISubFlow.class.isAssignableFrom(innerFlow.getClass());
        this.innerFlow = innerFlow;
        this.collector = collector;
        this.endCond = endCond;
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
        List<O1> results = new ArrayList<>();
        while(!this.endCond.apply(input, context)) {
            results.add(this.innerFlow.run(input, context));
        }
        return results.stream().collect(this.collector.apply(context));
    }
}
