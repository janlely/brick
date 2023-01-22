package org.brick.core;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.ClassUtils;
import org.brick.types.Either;

import java.util.concurrent.Flow;
import java.util.function.BiFunction;

public class AbortWhenFlow<I,O,C> implements SubFlow.ISubFlow<I, Either<I,O>,C> {

    private String desc;
    private BiFunction<I,C,Boolean> condChecker;
    private SubFlow.ISubFlow<I,O,C> abortFlow;

    public AbortWhenFlow(String desc,
                         BiFunction<I,C,Boolean> condChecker,
                         SubFlow.ISubFlow<I,O,C> abortFlow) {
        this.desc = desc;
        this.abortFlow = abortFlow;
        this.condChecker = condChecker;
    }

    @Override
    public FlowDoc<I, Either<I,O>, C> getFlowDoc() {
        FlowDoc<I,Either<I,O>,C> flowDoc = new FlowDoc<>(this.desc, FlowType.ABORT, getFlowName());
        Class<?>[] classes = TypeResolver.resolveRawArguments(AbortWhenFlow.class, this.getClass());
        flowDoc.add(this.abortFlow.getFlowDoc());
//        return flowDoc.types((Class<I>) classes[0], (Class<Either<I,O>>) classes[1], (Class<C>) classes[2]);
        return flowDoc;
    }

    @Override
    public Either<I,O> run(I input, C context) {
        return this.condChecker.apply(input, context)
                ? Either.right(abortFlow.run(input, context))
                : Either.left(input);
    }

    @Override
    public String getFlowName() {
        return SubFlow.ISubFlow.super.getFlowName() + ":" + ClassUtils.getShortClassName(AbortWhenFlow.class);
    }

    @Override
    public boolean isEnd() {
        return true;
    }
}
