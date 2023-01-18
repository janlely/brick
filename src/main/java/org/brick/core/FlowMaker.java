package org.brick.core;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.SerializationUtils;
import org.brick.types.Either;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class FlowMaker<I,O,C> {

    private List<Flow> flows;
    private ExecutorService executor;
    private FlowDoc<I,O,C> flowDoc;

    public FlowMaker(String desc) {
        this.flowDoc = new FlowDoc<>();
        this.flowDoc.desc = desc;
    }

    public FlowMaker<I,O,C> asyncExecutor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    //创建新的带Context的流程
    public Builder<I,O,C,I> flowBuilder() {
        this.flows = new ArrayList<>();
        return new Builder<>(this);
    }


    //流程构造器

    /**
     *
     * @param <I>
     * @param <O>
     * @param <C>
     * @param <T> type of output of current step
     */
    public static class Builder<I,O,C,T> {

        private FlowMaker<I,O,C> flowMaker;

        public Builder(FlowMaker<I,O,C> fLowMaker) {
            this.flowMaker = fLowMaker;
        }

        public Flow<I,T,C> build(Class<I> i, Class<O> t, Class<C> c) {
            return new SubFlow.ISubFlow<>() {
                @Override
                public FlowDoc<I, T, C> getFlowDoc() {
                    for (Flow flow : flowMaker.flows) {
                        flowMaker.flowDoc.add(flow.getFlowDoc());
                    }
                    return (FlowDoc<I, T, C>) flowMaker.flowDoc.types(i,t,c);
                }

                @Override
                public T run(I input, C context) {
                    Object i = input;
                    Object o = null;
                    for (Flow flow : flowMaker.flows) {
                        if (flow.isAsync()) {
                            final Object i1 = SerializationUtils.clone((Serializable) i);
                            flowMaker.executor.submit(() -> flow.run(i1, context));
                            continue;
                        }
                        if (flow.isEnd()) {
                            Either either = (Either) flow.run(i, context);
                            if (Either.isRight(either)) {
                                return (T) Either.getRight(either);
                            }
                            continue;
                        }
                        o = flow.run(i, context);
                        i = o;
                    }
                    return (T) o;
                }
            };
        }

        /**
         * insert a sub-flow
         * @param flow the flow
         * @param <O1> type of the output type of the flow
         * @param <F> type of the flow
         * @return
         */
        public <O1, F extends Flow<T,O1,C>> Builder<I,O,C,O1> flow(F flow) {
            assert SubFlow.ISubFlow.class.isAssignableFrom(flow.getClass());
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O1>) this;
        }

        public Builder<I,O,C,T> abort(AbortWhenFlow<T,O,C> flow) {
            this.flowMaker.flows.add(flow);
            PhantomFlow<T, C> phantomFlow = new PhantomFlow<>();
            return this;
        }

        public <O1, S extends ISideEffect<T,O1,C>> Builder<I,O,C,O1> effect(S flow) {
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O1>) this;
        }

        public <O1, P extends IPureFunction<T,O1,C>> Builder<I,O,C,O1> pure(P flow) {
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O1>) this;
        }

        public <I1 extends Serializable, O1, F extends Flow<I1,O1,C>> Builder<I,O,C,I1> subFlowAsync(F flow) {
            assert this.flowMaker.executor != null;
            assert SubFlow.ISubFlow.class.isAssignableFrom(flow.getClass());
            this.flowMaker.flows.add(new SubFlow.ISubFlow<I1,O1,C>() {
                @Override
                public FlowDoc getFlowDoc() {
                    return flow.getFlowDoc();
                }
                @Override
                public O1 run(I1 input, C context) {
                    return flow.run(input, context);
                }
                @Override
                public boolean isAsync() {
                    return true;
                }
                @Override
                public String getFlowType() {
                    return "Async: " + flow.getFlowType();
                }
            });
            return (Builder<I, O, C, I1>) this;
        }

    }
}
