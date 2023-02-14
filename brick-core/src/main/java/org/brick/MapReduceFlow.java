package org.brick;

import org.apache.commons.lang3.ClassUtils;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class MapReduceFlow<I,O,C,I1,O1,C1> implements SubFlow.ISubFlow<I,O,C> {

    private String desc;
    private Function<I, Stream<I1>> sourceFunc;
    private BiFunction<I,C,C1> contextFunc;
    private Flow<I1,O1,C1> mapFlow;
    private Collector<O1,?,O> collector;

    public MapReduceFlow(String desc, Function<I, Stream<I1>> sourceFunc,
                         BiFunction<I,C,C1> contextFunc, Flow<I1,O1,C1> mapFlow,
                         Collector<O1,?,O> collector) {
        this.desc = desc;
        this.sourceFunc = sourceFunc;
        this.contextFunc = contextFunc;
        this.mapFlow = mapFlow;
        this.collector = collector;
    }
    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        return new FlowDoc<>(this.desc, FlowType.SUB_FLOW, this.getFlowName());
    }

    @Override
    public O run(I input, C context) {
        return this.sourceFunc.apply(input)
                .map(a -> mapFlow.run(a, contextFunc.apply(input, context)))
                .collect(collector);
    }

    @Override
    public String getFlowName() {
        return SubFlow.ISubFlow.super.getFlowName() + ":" + ClassUtils.getShortClassName(MapReduceFlow.class);
    }
}
