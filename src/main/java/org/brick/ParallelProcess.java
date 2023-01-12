package org.brick;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collector;

public class ParallelProcess<I,O,C,E1,E2> implements IParallelProcess<I,O,C,E1,E2> {

    private BiFunction<I,C,List<E1>> lister;
    private BiFunction<E1,C,Boolean> filter;
    private BiFunction<E1,C,E2> mapper;
    private Collector<E2,?,O> collector;

    public ParallelProcess(BiFunction<I, C, List<E1>> lister,
                           BiFunction<E1, C, Boolean> filter,
                           BiFunction<E1, C, E2> mapper,
                           Collector<E2, ?, O> collector) {
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
}
