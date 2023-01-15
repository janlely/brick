package org.brick.lib.importflow;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.ClassUtils;
import org.brick.core.FlowDoc;
import org.brick.core.IPureFunction;
import org.brick.core.YesNoBranch;
import org.brick.types.Either;

import java.lang.annotation.Retention;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class PreCheckFlow<E1,E2,C> implements IPureFunction<List<E1>, Either<List<E2>,List<E1>>,C> {

    private BiFunction<E1,C, Optional<E2>> mapper;
    private String desc;

    public PreCheckFlow(String desc, BiFunction<E1,C,Optional<E2>> mapper) {
        this.desc = desc;
        this.mapper = mapper;
    }

    @Override
    public FlowDoc<List<E1>, Either<List<E2>, List<E1>>, C> getFlowDoc() {
        FlowDoc<List<E1>,Either<List<E2>, List<E1>>,C> flowDoc = new FlowDoc<>(this.desc, getFlowType());
        Class<?>[] classes = TypeResolver.resolveRawArguments(PreCheckFlow.class, this.getClass());
        return flowDoc.types((Class<List<E1>>) classes[0], (Class<Either<List<E2>, List<E1>>>) classes[1], (Class<C>) classes[2]);
    }

    @Override
    public Either<List<E2>, List<E1>> pureCalculate(List<E1> input, C context) {
        List<E2> errors = input.parallelStream()
                .map(e -> this.mapper.apply(e, context))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        return errors.isEmpty() ? Either.right(input) : Either.left(errors);
    }

    @Override
    public String getFlowType() {
        return IPureFunction.super.getFlowType() + ":" + ClassUtils.getShortClassName(PreCheckFlow.class);
    }
}
