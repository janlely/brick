package org.brick.model;

import org.apache.commons.lang3.ClassUtils;
import org.brick.FlowDoc;
import org.brick.FlowType;

import java.util.function.BiFunction;

public class QueryDBFlow<I,O,C> implements IQueryDBFlow<I,O,C>{

    private String desc;
    private String sql;
    private BiFunction<I,C,O> func;

    public QueryDBFlow(String desc, String sql, BiFunction<I,C,O> func) {
        this.desc = desc;
        this.sql = sql;
        this.func = func;
    }
    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, FlowType.EFFECT, this.getFlowName());
//        Class<?>[] classes = TypeResolver.resolveRawArguments(QueryDBFlow.class, this.getClass());
//        return flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
        return flowDoc;
    }

    @Override
    public O doDBQuery(I input, C context) {
        return this.func.apply(input, context);
    }

    @Override
    public String getFlowName() {
        return IQueryDBFlow.super.getFlowName() + ":" + ClassUtils.getShortClassName(QueryDBFlow.class);
    }
}
