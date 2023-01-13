package org.brick.core;

import net.jodah.typetools.TypeResolver;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class FlowMaker<I,O,C> {

    private List<Flow> flows;
    private ExecutorService executor;
    private FlowDoc<I,O,C> flowDoc;

    public FlowMaker(ExecutorService executor) {
        this.executor = executor;
        this.flowDoc = new FlowDoc<>();
    }

    public FlowMaker<I,O,C> withDesc(String desc) {
        this.flowDoc.desc = desc;
        return this;
    }

    //创建新的带Context的流程
    public Builder<I,O,C,I> flowBuilder() {
        this.flows = new ArrayList<>();
        return new Builder<>(this, null);
    }

    //流程终结者
    public static class Finisher<I,O,C> {

        private FlowMaker<I,O,C> flowMaker;
        private Class<O> cls;

        public Finisher(FlowMaker<I,O,C> fLowMaker, Class<O> cls) {
            this.flowMaker = fLowMaker;
            this.cls = cls;
        }
        public Flow<I,O,C> build() {
            return new Flow<I, O, C>() {
                @Override
                public FlowDoc<I, O, C> getFlowDoc() {
                    for (Flow flow : flowMaker.flows) {
                        flowMaker.flowDoc.add(flow.getFlowDoc());
                    }
                    Class<?>[] classes = TypeResolver.resolveRawArguments(FlowMaker.class, flowMaker.getClass());
                    return flowMaker.flowDoc.types((Class<I>)classes[0], (Class<O>)classes[1], (Class<C>)classes[2]);
                }

                @Override
                public String getFlowType() {
                    return "By FlowMaker";
                }

                @Override
                public O run(I input, C context) {
                    Object i = input;
                    Object o = null;
                    for (Flow flow : flowMaker.flows) {
                        if (flow.isAsync()) {
                            final Object i1 = SerializationUtils.clone((Serializable) i);
                            flowMaker.executor.submit(() -> flow.run(i1, context));
                            continue;
                        }
                        o = flow.run(i, context);
                        i = o;
                    }
                    return (O) o;
                }
            };
        }
    }

    //流程构造器
    public static class Builder<I,O,C,T> {

        private FlowMaker<I,O,C> flowMaker;
        private Class<T> cls;


        public Builder(FlowMaker<I,O,C> fLowMaker, Class<T> cls) {
            this.flowMaker = fLowMaker;
            this.cls = cls;
        }

        public <O1> Builder<I,O,C,O1> subFlow(Flow<T,O1,C> flow) {
            this.flowMaker.flows.add(flow);
            Class<?>[] classes = TypeResolver.resolveRawArguments(Flow.class, flow.getClass());
            return new Builder<>(this.flowMaker, (Class<O1>) classes[1]);
        }

        public <O1, TC extends ISideEffect> Builder<I,O,C,O1> effect(Class<TC> targetClass, Flow<T,O1,C> flow) {
            this.flowMaker.flows.add(flow);
            Class<?>[] classes = TypeResolver.resolveRawArguments(targetClass, flow.getClass());
            return new Builder<>(this.flowMaker, (Class<O1>) classes[1]);
        }

        public <O1, TC extends IPureFunction> Builder<I,O,C,O1> pure(Class<TC> targetClass, Flow<T,O1,C> flow) {
            this.flowMaker.flows.add(flow);
            Class<?>[] classes = TypeResolver.resolveRawArguments(targetClass, flow.getClass());
            return new Builder<>(this.flowMaker, (Class<O1>) classes[1]);
        }

        public <O1, TC extends Flow> Builder<I,O,C,O1> subFlow(Class<TC> targetClass, Flow<T,O1,C> flow) {
            this.flowMaker.flows.add(flow);
            Class<?>[] classes = TypeResolver.resolveRawArguments(targetClass, flow.getClass());
            return new Builder<>(this.flowMaker, (Class<O1>) classes[1]);
        }

        public <I1 extends Serializable> Builder<I,O,C,I1> async(IAsyncFlow<I1,?,C> flow) {
            this.flowMaker.flows.add(flow);
            return (Builder<I, O, C, I1>) this;
        }

        public Finisher<I,O,C> last(Flow<T,O,C> flow) {
            this.flowMaker.flows.add(flow);
            Class<?>[] classes = TypeResolver.resolveRawArguments(Flow.class, flow.getClass());
            return new Finisher<>(this.flowMaker, (Class<O>) classes[1]);
        }


        public <TC> Finisher<I,O,C> last(Class<TC> targetClass, Flow<T,O,C> flow) {
            this.flowMaker.flows.add(flow);
            Class<?>[] classes = TypeResolver.resolveRawArguments(targetClass, flow.getClass());
            return new Finisher<>(this.flowMaker, (Class<O>) classes[1]);
        }

    }
}
