package io.github.janlely.brick.core;

import io.github.janlely.brick.common.types.Either;
import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiFunction;

/**
 * just like if-return
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context
 */
public class AbortWhenFlow<I,O,C> implements SubFlow.ISubFlow<I, Either<I,O>,C> {

    /**
     * description
     */
    private String desc;
    /**
     * condition checker
     */
    private BiFunction<I,C,Boolean> condChecker;
    /**
     * the abort flow
     */
    private Flow<I,O,C> abortFlow;

    /**
     * @param desc the description
     * @param condChecker the condition checker
     * @param abortFlow the abort flow
     */
    public AbortWhenFlow(String desc,
                         BiFunction<I,C,Boolean> condChecker,
                         Flow<I,O,C> abortFlow) {

        assert SubFlow.ISubFlow.class.isAssignableFrom(abortFlow.getClass());
        this.desc = desc;
        this.abortFlow = abortFlow;
        this.condChecker = condChecker;
    }

    @Override
    public FlowDoc<I, Either<I,O>, C> getFlowDoc() {
        FlowDoc<I,Either<I,O>,C> flowDoc = new FlowDoc<>(this.desc, FlowType.ABORT, getFlowName());
        flowDoc.add(this.abortFlow.getFlowDoc());
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
