package org.brick.core;

import net.jodah.typetools.TypeResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * @param <I> input type
 * @param <O> output type
 * @param <C> context type
 * @param <P> pattern type
 */
public class CaseBranch<I,O,C,P> implements IMultiBranch<I,O,C,P> {

    private Map<P, SubFlow.ISubFlow> flowMap = new HashMap<>();
    private Function<I,P> caseValueFunc;
    private String desc;

    public CaseBranch(String desc, Function<I,P> caseValueFunc, CaseFlow<I,O,C,P> ...flows) {
        this.desc = desc;
        this.caseValueFunc = caseValueFunc;
        for (CaseFlow<I,O,C,P> flow : flows) {
            flowMap.put(flow.getValue(), flow.getFlow());
        }
    }

    @Override
    public P pattern(I input) {
        return caseValueFunc.apply(input);
    }

    @Override
    public SubFlow.ISubFlow<I, O, C> select(P value) {
        return flowMap.get(value);
    }

    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc);
        Class<?>[] classes = TypeResolver.resolveRawArguments(CaseBranch.class, this.getClass());
        for (Flow flow : this.flowMap.values()) {
            flowDoc.add(flow.getFlowDoc());
        }
        return flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
    }
}
