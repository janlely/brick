package io.github.janlely.brick.core;

import io.github.janlely.brick.common.types.Pair;
import org.apache.commons.lang3.ClassUtils;

import java.util.List;
import java.util.function.BiFunction;

/**
 * the foldl flow, like foldl in haskell
 * @param <I> the input type
 * @param <ID> the output type, type of 'b' int haskell's foldl
 * @param <C> the context type
 */
public class FoldlFlow<I,ID,C> implements Flow<List<I>,ID,C>{

    /**
     * the description
     */
    private String desc;
    /**
     * the foldl function flow
     */
    private Flow<Pair<ID,I>,ID,C> foldFunc;
    /**
     * the init value of ID
     */
    private ID id;

    /**
     * @param desc the description
     * @param id the init value of ID
     * @param foldFlow the fold flow
     */
    public FoldlFlow(String desc, ID id, Flow<Pair<ID,I>,ID,C> foldFlow) {
        this.desc = desc;
        this.foldFunc = foldFlow;
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
            initId = foldFunc.run(new Pair<>(initId, i), context);
        }
        return initId;
    }
}
