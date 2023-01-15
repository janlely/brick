package org.brick.lib.importflow;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.ClassUtils;
import org.brick.core.FlowDoc;
import org.brick.core.IPureFunction;
import org.brick.types.Pair;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 *
 * @param <E> Type of element
 * @param <C>
 */
public class GetPrepareActionsFlow<E,C> implements IPureFunction<List<E>, Pair<List<E>,List<ActionInfo>>, C> {

    private String desc;
    private BiFunction<E,C,List<ActionInfo>> func;
    private Combinators combinators;

    public GetPrepareActionsFlow(String desc,
                                 BiFunction<E,C,List<ActionInfo>> func,
                                 Combinators combinators) {
        this.desc = desc;
        this.func = func;
        this.combinators = combinators;
    }

    @Override
    public FlowDoc<List<E>, Pair<List<E>, List<ActionInfo>>, C> getFlowDoc() {
        FlowDoc<List<E>, Pair<List<E>, List<ActionInfo>>, C> flowDoc = new FlowDoc<>(this.desc, getFlowType());
        Class<?>[] classes = TypeResolver.resolveRawArguments(PreCheckFlow.class, this.getClass());
        return flowDoc.types((Class<List<E>>) classes[0], (Class<Pair<List<E>, List<ActionInfo>>>) classes[1], (Class<C>) classes[2]);
    }

    @Override
    public Pair<List<E>, List<ActionInfo>> pureCalculate(List<E> input, C context) {
        return new Pair<>(input, input.parallelStream()
                .flatMap(e -> this.func.apply(e, context).stream())
                .collect(Collectors.groupingBy(ActionInfo::getType))
                .entrySet()
                .stream()
                .flatMap(entry -> combinators.getCombinator(entry.getKey()).combine(entry.getValue()).stream())
                .collect(Collectors.toList()));
    }

    @Override
    public String getFlowType() {
        return IPureFunction.super.getFlowType() + ":" + ClassUtils.getShortClassName(GetPrepareActionsFlow.class);
    }
}
