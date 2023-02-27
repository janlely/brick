package org.brick;

import org.apache.commons.lang3.ClassUtils;

import java.util.List;
import java.util.function.BiFunction;

public class FoldlFlow<I,ID,C> implements Flow<List<I>,ID,C>{

    private String desc;
    private BiFunction<ID,I,ID> foldFunc;
    private ID id;

    public FoldlFlow(String desc, ID id, BiFunction<ID,I,ID> foldFunc) {
        this.desc = desc;
        this.foldFunc = foldFunc;
        this.id = id;
    }

    @Override
    public FlowDoc<List<I>, ID, C> getFlowDoc() {
        return new FlowDoc<>(this.desc, FlowType.FOLDL_FLOW, this.getFlowName());
    }

    @Override
    public String getFlowName() {
        return ClassUtils.getShortClassName(FoldlFlow.class);
    }

    @Override
    public ID run(List<I> input, C context) {
        ID initId = this.id;
        for (I i : input) {
            initId = foldFunc.apply(initId, i);
        }
        return initId;
    }
}
