package org.brick.model;

import net.jodah.typetools.TypeResolver;
import org.brick.core.CaseFlow;
import org.brick.core.Flow;
import org.brick.core.FlowDoc;
import org.brick.core.IMultiBranch;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CaseBranch<I,O,C,P> implements IMultiBranch<I,O,C,P> {

    private Map<P, Flow> flowMap = new HashMap<>();
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
    public Flow<I, O, C> select(P value) {
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
