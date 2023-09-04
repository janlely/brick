package io.github.janlely.brick.core;

import org.apache.commons.lang3.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;


/**
 * @param <I> input type
 * @param <O> output type
 * @param <C> context type
 * @param <P> pattern type
 */
public class CaseBranch<I,O,C,P> implements IMultiBranchFlow<I,O,C,P> {

    /**
     * the flow map
     */
    private Map<P, SubFlow.ISubFlow<I,O,C>> flowMap = new HashMap<>();
    /**
     * the pattern generator
     */
    private BiFunction<I,C,P> caseValueFunc;
    /**
     * the description
     */
    private String desc;

    /**
     * @param desc the description
     * @param caseValueFunc the function to generate case value
     * @param flows the case flows
     */
    public CaseBranch(String desc, BiFunction<I,C,P> caseValueFunc, CaseFlow<I,O,C,P> ...flows) {
        this.desc = desc;
        this.caseValueFunc = caseValueFunc;
        for (CaseFlow<I,O,C,P> flow : flows) {
            for (P p : flow.getValue()) {
                flowMap.put(p, flow.getFlow());
            }
        }
    }

    @Override
    public String getFlowName() {
        return IMultiBranchFlow.super.getFlowName() + ":" + ClassUtils.getShortClassName(CaseBranch.class);
    }

    @Override
    public P pattern(I input, C context) {
        return caseValueFunc.apply(input, context);
    }

    @Override
    public SubFlow.ISubFlow<I, O, C> select(P value) {
        return flowMap.get(value);
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, FlowType.BRANCH, this.getFlowName());
        for (Flow<I,O,C> flow : this.flowMap.values()) {
            flowDoc.add(flow.getFlowDoc());
        }
        return flowDoc;
    }
}
