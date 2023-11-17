package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * map flow
 * @param <I>
 * @param <O>
 * @param <C>
 */
public class MapFlow<I,O,C> implements Flow<List<I>, List<O>, C>{

    /**
     * the description
     */
    private String desc;

    /**
     * the map function
     */
    private Flow<I,O,C> mapFunc;

    /**
     * @param desc the description
     * @param mapFunc the map function
     */
    public MapFlow(String desc, Flow<I,O,C> mapFunc) {
        this.desc = desc;
        this.mapFunc = mapFunc;
    }
    @Override
    public FlowDoc<List<I>, List<O>, C> getFlowDoc() {
        return new FlowDoc<>(this.desc, FlowType.MAP_FLOW, this.getFlowName());
    }

    @Override
    public String getFlowName() {
        return ClassUtils.getShortClassName(MapFlow.class);
    }

    @Override
    public List<O> run(List<I> input, C context) {
        return input.stream().map(i -> mapFunc.run(i, context)).collect(Collectors.toList());
    }
}
