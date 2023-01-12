package org.brick.model;

import org.brick.core.CaseFlow;
import org.brick.core.IFlow;
import org.brick.core.IMultiBranch;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CaseBatch<I,O,C,P> implements IMultiBranch<I,O,C,P> {

    private Map<P, IFlow> flowMap = new HashMap<>();
    private Function<I,P> caseValueFunc;

    public CaseBatch(Function<I,P> caseValueFunc, CaseFlow<I,O,C,P> ...flows) {
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
    public IFlow<I, O, C> select(P value) {
        return null;
    }

}
