package org.brick.model;

import org.apache.commons.lang3.ClassUtils;
import org.brick.FlowDoc;
import org.brick.FlowType;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collector;

public class ParallelFlow<I,O,C,E1,E2> implements IParallelFlow<I,O,C,E1,E2> {

    private BiFunction<I,C,List<E1>> lister;
    private BiFunction<E1,C,Boolean> filter1;
    private BiFunction<E2,C,Boolean> filter2;
    private BiFunction<E1,C,E2> mapper;
    private Collector<E2,?,O> collector;
    private String desc;

    public ParallelFlow(String desc, BiFunction<I, C, List<E1>> lister,
                        BiFunction<E1, C, Boolean> filter1,
                        BiFunction<E1, C, E2> mapper,
                        BiFunction<E2, C, Boolean> filter2,
                        Collector<E2, ?, O> collector) {
        this.desc = desc;
        this.lister = lister;
        this.filter1 = filter1;
        this.filter2 = filter2;
        this.mapper = mapper;
        this.collector = collector;
    }


    @Override
    public List<E1> toList(I input, C context) {
        return this.lister.apply(input, context);
    }

    @Override
    public boolean filter1(E1 elem, C context) {
        return this.filter1.apply(elem, context);
    }

    @Override
    public boolean filter2(E2 elem, C context) {
        return this.filter2.apply(elem, context);
    }

    @Override
    public E2 mapper(E1 elem, C context) {
        return this.mapper.apply(elem, context);
    }

    @Override
    public Collector<E2, ?, O> collector(C context) {
        return this.collector;
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, FlowType.PURE_FUNCTION, this.getFlowName());
//        Class<?>[] classes = TypeResolver.resolveRawArguments(YesNoBranch.class, this.getClass());
//        return flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
        return flowDoc;
    }

    @Override
    public String getFlowName() {
        return IParallelFlow.super.getFlowName() + ":" + ClassUtils.getShortClassName(ParallelFlow.class);
    }
}
