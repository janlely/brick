package org.brick;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.brick.common.types.Either;
import org.brick.exception.ExceptionHandler;
import org.brick.exception.FlowException;

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
                        for (Flow flow : flowMaker.flows) {
                            if (flow.getClass().equals(ModifyContext.class)) {
                                context = (C) ModifyContext.class.cast(flow).mod(i, context);
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
                            i = flow.run(i, context);
                        }
                        return (T) i;
                    } catch (FlowException e) {
                        if (!flowMaker.exceptionHandlers.containsKey(e.getType())) {
                            System.out.println("unhandled FlowException by this flow of type: " + e.getType());
                            throw e;
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
         * insert a map-reduce flow
         * @param flow
         * @return
         * @param <O1>
         */
        public <O1> Builder<I,O,C,O1> mapReduce(MapReduceFlow<T,O1,C,?,?,?> flow) {
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

        public Builder<I,O,C,T> throwWhen(ThrowWhenFlow<T,C> flow) {
            this.flowMaker.flows.add(flow);
            return this;
        }

        /**
         * modify context
         * like Reader.local in haskell
         * @param modifyFlow
         * @return
         */
        public Builder<I,O,C,T> local(ModifyContext<T,C> modifyFlow) {
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
