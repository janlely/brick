package org.brick.model;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.ClassUtils;
import org.brick.core.FlowDoc;
import org.brick.core.YesNoBranch;
import org.brick.model.IParallelFlow;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collector;

public class ParallelFlow<I,O,C,E1,E2> implements IParallelFlow<I,O,C,E1,E2> {

    private BiFunction<I,C,List<E1>> lister;
    private BiFunction<E1,C,Boolean> filter;
    private BiFunction<E1,C,E2> mapper;
    private Collector<E2,?,O> collector;
    private String desc;

    public ParallelFlow(String desc, BiFunction<I, C, List<E1>> lister,
                        BiFunction<E1, C, Boolean> filter,
                        BiFunction<E1, C, E2> mapper,
                        Collector<E2, ?, O> collector) {
        this.desc = desc;
        this.lister = lister;
        this.filter = filter;
        this.mapper = mapper;
        this.collector = collector;
    }


    @Override
    public List<E1> toList(I input, C context) {
        return this.lister.apply(input, context);
    }

    @Override
    public boolean filter(E1 elem, C context) {
        return this.filter.apply(elem, context);
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
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, getFlowType());
        Class<?>[] classes = TypeResolver.resolveRawArguments(YesNoBranch.class, this.getClass());
        return flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
    }

    @Override
    public String getFlowType() {
        return IParallelFlow.super.getFlowType() + ":" + ClassUtils.getShortClassName(ParallelFlow.class);
    }
}
