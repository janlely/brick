package org.brick.core;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.brick.exception.ExceptionHandler;
import org.brick.exception.FlowException;
import org.brick.types.Either;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class FlowMaker<I,O,C> {

    private List<Flow> flows;
    private ExecutorService executor;
    private FlowDoc<I,O,C> flowDoc;

    private Map<Integer, ExceptionHandler> exceptionHandlers;

    public FlowMaker(String desc) {
        this.flowDoc = new FlowDoc<>(desc, FlowType.SUB_FLOW, ClassUtils.getShortClassName(FlowMaker.class));
        this.exceptionHandlers = new HashMap<>();
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

        public Flow<I,T,C> build() {
            return new SubFlow.ISubFlow<>() {
                @Override
                public FlowDoc<I, T, C> getFlowDoc() {
                    for (Flow flow : flowMaker.flows) {
                        flowMaker.flowDoc.add(flow.getFlowDoc());
                    }
                    return (FlowDoc<I, T, C>) flowMaker.flowDoc;
                }

                @Override
                public T run(I input, C context) {
                    try {
                        Object i = input;
                        Object o = null;
                        for (Flow flow : flowMaker.flows) {
                            if (flow.getClass().equals(ModifyContext.class)) {
                                context = (C) ModifyContext.class.cast(flow).modify(context);
                                continue;
                            }
                            if (flow.isAsync()) {
                                final Object i1 = SerializationUtils.clone((Serializable) i);
                                C finalContext = context;
                                flowMaker.executor.submit(() -> flow.run(i1, finalContext));
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
                    } catch (FlowException e) {
                        if (!flowMaker.exceptionHandlers.containsKey(e.getType())) {
                            throw new RuntimeException("No ExceptionHandler for FlowException of type: " + e.getType(), e.getCause());
                        }
                        return (T) flowMaker.exceptionHandlers.get(e.getType()).handler(e.getContent());
                    }
                }
            };
        }

        public Builder<I,O,C,T> exception(int type, ExceptionHandler<O> handler) {
            this.flowMaker.exceptionHandlers.putIfAbsent(type, handler);
            return this;
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

        /**
         * like if-return
         * @param flow
         * @return
         */
        public Builder<I,O,C,T> abort(AbortWhenFlow<T,O,C> flow) {
            this.flowMaker.flows.add(flow);
            return this;
        }

        /**
         * modify context
         * like Reader.local in haskell
         * @param modifyFlow
         * @return
         */
        public Builder<I,O,C,T> local(ModifyContext<I,C> modifyFlow) {
            this.flowMaker.flows.add(modifyFlow);
            return this;
        }

        /**
         * loop a flow
         * @param flow
         * @return
         */
        public Builder<I,O,C,O> loop(LoopFlow<T,O,C,?> flow) {
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O>) this;
        }

        /**
         * add a effect flow into flow list
         * @param flow
         * @param <O1>
         * @param <S>
         * @return
         */
        public <O1, S extends ISideEffect<T,O1,C>> Builder<I,O,C,O1> effect(S flow) {
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O1>) this;
        }

        /**
         * add a pure function into flow list
         * @param flow
         * @param <O1>
         * @param <P>
         * @return
         */
        public <O1, P extends IPureFunction<T,O1,C>> Builder<I,O,C,O1> pure(P flow) {
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O1>) this;
        }

        /**
         * add a async flow
         * @param flow
         * @param <I1>
         * @param <O1>
         * @param <F>
         * @return
         */
        public <I1 extends Serializable, O1, F extends Flow<I1,O1,C>> Builder<I,O,C,I1> flowAsync(F flow) {
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
                public String getFlowName() {
                    return "Async: " + flow.getFlowName();
                }
            });
            return (Builder<I, O, C, I1>) this;
        }


    }
}
