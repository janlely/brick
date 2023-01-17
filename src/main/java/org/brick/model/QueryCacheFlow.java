package org.brick.model;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.ClassUtils;
import org.brick.core.FlowDoc;

import java.util.function.BiFunction;

public class QueryCacheFlow<I,O,C> implements IQueryCacheFlow<I,O,C>{

    private String desc;
    private String key;
    private String type;
    private BiFunction<I,C,O> func;

    public QueryCacheFlow(String desc, String key, String type, BiFunction<I,C,O> func) {
        this.desc = desc;
        this.key = key;
        this.type = type;
        this.func = func;
    }
    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, getFlowType());
        Class<?>[] classes = TypeResolver.resolveRawArguments(QueryCacheFlow.class, this.getClass());
        return flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
    }

    @Override
    public O doQueryCache(I input, C context) {
        return this.func.apply(input, context);
    }

    @Override
    public String getFlowType() {
        return IQueryCacheFlow.super.getFlowType() + ":" + ClassUtils.getShortClassName(QueryCacheFlow.class);
    }
}
