package io.github.janlely.brick.core;

import io.github.janlely.brick.common.types.Either;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SerializationUtils;
import io.github.janlely.brick.core.exception.ErrorHandler;
import io.github.janlely.brick.core.exception.FlowError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * the flow maker
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context
 */
public class FlowMaker<I,O,C> {

    /**
     * the flows
     */
    private List<Flow> flows;
    /**
     * the executor service
     */
    private ExecutorService executor;
    /**
     * the doc
     */
    private FlowDoc<I,O,C> flowDoc;

    /**
     * the exception handlers
     */
    private Map<Integer, ErrorHandler> exceptionHandlers;

    /**
     * @param desc the description
     */
    public FlowMaker(String desc) {
        this.flowDoc = new FlowDoc<>(desc, FlowType.SUB_FLOW, ClassUtils.getShortClassName(FlowMaker.class));
        this.exceptionHandlers = new HashMap<>();
    }

    /**
     * @param executor the executor service
     * @return this
     */
    public FlowMaker<I,O,C> asyncExecutor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    /**
     * @return the flow buidler
     */
    public Builder<I,O,C,I> flowBuilder() {
        this.flows = new ArrayList<>();
        return new Builder<>(this);
    }


    /**
     * the flow buidler
     * @param <I> the input type
     * @param <O> the final output type
     * @param <C> the context type
     * @param <T> the sub output type
     */
    public static class Builder<I,O,C,T> {

        /**
         * the flow maker
         */
        private FlowMaker<I,O,C> flowMaker;

        /**
         * @param fLowMaker the flow maker
         */
        public Builder(FlowMaker<I,O,C> fLowMaker) {
            this.flowMaker = fLowMaker;
        }

        /**
         * @return the flow
         */
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
                        for (Flow flow : flowMaker.flows) {
                            if (flow.getClass().equals(ModifyContext.class)) {
                                context = (C) ModifyContext.class.cast(flow).mod(i, context);
                                continue;
                            }
                            if (flow.isAsync()) {
                                C finalContext = context;
                                final Object i1 = i;
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
                            i = flow.run(i, context);
                        }
                        return (T) i;
                    } catch (FlowError e) {
                        if (!flowMaker.exceptionHandlers.containsKey(e.getType())) {
                            System.out.println("unhandled FlowException by this flow of type: " + e.getType());
                            throw e;
                        }
                        return (T) flowMaker.exceptionHandlers.get(e.getType()).handler(e.getContent());
                    }
                }
            };
        }

        /**
         * @param type the custom type of the exception
         * @param handler the exception handler
         * @return this
         */
        public Builder<I,O,C,T> onError(int type, ErrorHandler<O> handler) {
            this.flowMaker.exceptionHandlers.putIfAbsent(type, handler);
            return this;
        }

        /**
         * @param flow add a sub flow
         * @param <O1> the output type of the sub flow
         * @param <F> the type of the sub flow
         * @return this
         */
        public <O1, F extends Flow<T,O1,C>> Builder<I,O,C,O1> flow(F flow) {
            assert SubFlow.ISubFlow.class.isAssignableFrom(flow.getClass());
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O1>) this;
        }

        /**
         * @param flow add a sub flow
         * @param <O1> the output type of the sub flow
         * @param <F> the type of the sub flow
         * @return this
         */
        public <O1, F extends Flow<T,O1,C>> Builder<I,O,C,O1> branch(F flow) {
            assert IYesNoBranchFlow.class.isAssignableFrom(flow.getClass()) || IMultiBranchFlow.class.isAssignableFrom(flow.getClass());
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O1>) this;
        }

        /**
         * insert a map-reduce flow
         * @param flow the map-reduce flow
         * @param <O1> the type of the map-reduce flow
         * @return this
         */
        public <O1> Builder<I,O,C,O1> mapReduce(MapReduceFlow<T,O1,C,?,?,?> flow) {
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O1>) this;
        }

        /**
         * add a foldl
         * @param foldlFlow the fold flow
         * @param <O1> the type of the fold flow
         * @return this
         */
        public <O1> Builder<I,O,C,O1> foldl(Flow<T,O1,C> foldlFlow) {
            assert foldlFlow.getClass().equals(FoldlFlow.class);
            this.flowMaker.flows.add(foldlFlow);
            return (Builder<I, O, C, O1>) this;
        }

        /**
         * like if-return
         * @param flow the abort flow
         * @return this
         */
        public Builder<I,O,C,T> abort(AbortWhenFlow<T,O,C> flow) {
            this.flowMaker.flows.add(flow);
            return this;
        }

        /**
         * @param flow the error to throw
         * @return this
         */
        public Builder<I,O,C,T> throwWhen(ThrowWhenFlow<T,C> flow) {
            this.flowMaker.flows.add(flow);
            return this;
        }

        /**
         * modify context
         * like Reader.local in haskell
         * @param modifyFlow the context modifier
         * @return this
         */
        public Builder<I,O,C,T> local(ModifyContext<T,C> modifyFlow) {
            this.flowMaker.flows.add(modifyFlow);
            return this;
        }

        /**
         * loop a flow
         * @param flow the loop flow
         * @return this
         */
        public <O1> Builder<I,O,C,O1> loop(LoopFlow<T,O1,C> flow) {
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O1>) this;
        }

        /**
         * add a effect flow into flow list
         * @param flow the effect flow
         * @param <O1> the output type of the effect flow
         * @param <S> the type of the effect flow
         * @return this
         */
        public <O1, S extends ISideEffect<T,O1,C>> Builder<I,O,C,O1> effect(S flow) {
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O1>) this;
        }

        /**
         * add a trace flow
         * @param traceFlow the trace flow
         * @return this
         */
        public Builder<I,O,C,T> trace(TraceFlow<T,C> traceFlow) {
            this.flowMaker.flows.add(traceFlow);
            return this;
        }

        /**
         * add a pure function into flow list
         * @param flow the pure function
         * @param <O1> the output type of the pure
         * @param <P> the type of the pure
         * @return this
         */
        public <O1, P extends IPureFunction<T,O1,C>> Builder<I,O,C,O1> pure(P flow) {
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, O1>) this;
        }

        /**
         * add a count down flow
         * @param countDownFlow the flow
         * @param <O1> the output type of the count down
         * @return this
         */
        public <O1> Builder<I,O,C,O1> countDown(CountDownFlow<T,O1,?,C> countDownFlow) {
            this.flowMaker.flows.add(countDownFlow);
            return (Builder<I, O, C, O1>) this;
        }
        /**
         * add a async flow
         * @param flow the type of async flow
         * @param <O1> the output type of the async flow
         * @param <F> the type of the async flow
         * @return
         */
        public <O1, F extends Flow<T,O1,C>> Builder<I,O,C,T> async(F flow) {
            assert this.flowMaker.executor != null;
            assert SubFlow.ISubFlow.class.isAssignableFrom(flow.getClass());
            this.flowMaker.flows.add(new SubFlow.ISubFlow<T,O1,C>() {
                @Override
                public FlowDoc getFlowDoc() {
                    return flow.getFlowDoc();
                }
                @Override
                public O1 run(T input, C context) {
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
            return this;
        }


    }
}
