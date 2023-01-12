package org.brick;

import net.jodah.typetools.TypeResolver;

import java.util.ArrayList;
import java.util.List;

public class FlowMaker<I,O,C> {

    private List<Flow> flows;
    private C context;

    //创建新的带Context的流程
    public Builder<I,O,C,I> newFlowWithContext(C context) {
        this.flows = new ArrayList<>();
        this.context = context;
        return new Builder<>(this, null);
    }

    public static class Builder<I,O,C,T> {

        private FlowMaker<I,O,C> flowMaker;
        private Class<T> cls;

        public Builder(FlowMaker<I,O,C> fLowMaker, Class<T> cls) {
            this.flowMaker = fLowMaker;
            this.cls = cls;
        }

        Flow<I,O> build() {
            return input -> {
                Object i = input;
                Object o = null;
                for (Flow flow : flowMaker.flows) {
                    o = flow.run(i);
                    i = o;
                }
                return (O) o;
            };
        }

        //添加纯计算
        <O1> Builder<I,O,C,O1> nextPure(PureProcess<T,O1,C> pureProcess) {
            this.flowMaker.flows.add((Flow<T, O1>) input -> pureProcess.process(input, flowMaker.context));
            Class<?>[] classes = TypeResolver.resolveRawArguments(PureProcess.class, pureProcess.getClass());
            return new Builder<>(this.flowMaker, (Class<O1>) classes[1]);
        }

        <O1> Builder<I,O,C,O1> nextYesNoBranch(YesNoBranchProcess<T,O1,C> yesNoBranchProcess) {
            this.flowMaker.flows.add((Flow<T,O1>) input -> {
                if (yesNoBranchProcess.isYes(input, flowMaker.context)) {
                    return (O1) yesNoBranchProcess.yes(flowMaker.context).run(input);
                }
                return (O1) yesNoBranchProcess.no(flowMaker.context).run(input);
            });
            Class<?>[] classes = TypeResolver.resolveRawArguments(YesNoBranchProcess.class, yesNoBranchProcess.getClass());
            return new Builder<>(this.flowMaker, (Class<O1>) classes[1]);
        }

        //添加副作用
        DGraph.Node nextSideEffect() {
            return null;
        }

        //添加并行计算
        DGraph.Node nextParallel() {

            return null;
        }

        //添加串行计算
        DGraph.Node nextSequence() {
            return null;
        }

        //更新context
        DGraph.Node updateContext() {

            return null;
        }

        //添加异步旁路
        DGraph.Node addByPassAsync() {

            return null;
        }


    }
}
