package org.brick;

import net.jodah.typetools.TypeResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class FlowMaker<I,O,C> {

    private List<IFlow> flows;
    private C context;

    //创建新的带Context的流程
    public Builder<I,O,C,I> newFlowWithContext(C context) {
        this.flows = new ArrayList<>();
        this.context = context;
        return new Builder<>(this, null);
    }

    public static Map<Class, BiFunction<IProcess, Object, IFlow>> flowMap;

    static {
        flowMap.put(IPureProcess.class, (process,context) ->
                input -> IPureProcess.class.cast(process).process(input, context));
        flowMap.put(IYesNoBranchProcess.class, (process,context) ->
                input -> {
                    IYesNoBranchProcess yesNoBranch = IYesNoBranchProcess.class.cast(process);
                    if (yesNoBranch.isYes(input, context)) {
                        return yesNoBranch.yes(context).run(input);
                    }
                    return yesNoBranch.no(context).run(input);
                });
        flowMap.put(IParallelProcess.class, ((process, context) ->
                input -> {
                    IParallelProcess parallelProcess = IParallelProcess.class.cast(process);
                    Predicate predicate = e -> parallelProcess.filter(e, context);
                    Function mapper = e -> parallelProcess.mapper(e, context);
                    return parallelProcess.toList(input, context)
                            .parallelStream()
                            .filter(predicate)
                            .map(mapper)
                            .collect(parallelProcess.collector(context));
                }));
    }

    public static class Finisher<I,O,C> {

        private FlowMaker<I,O,C> flowMaker;
        private Class<O> cls;

        public Finisher(FlowMaker<I,O,C> fLowMaker, Class<O> cls) {
            this.flowMaker = fLowMaker;
            this.cls = cls;
        }
        public IFlow<I,O> build() {
            return input -> {
                Object i = input;
                Object o = null;
                for (IFlow IFlow : flowMaker.flows) {
                    o = IFlow.run(i);
                    i = o;
                }
                return (O) o;
            };
        }
    }

    public static class Builder<I,O,C,T> {

        private FlowMaker<I,O,C> flowMaker;
        private Class<T> cls;


        public Builder(FlowMaker<I,O,C> fLowMaker, Class<T> cls) {
            this.flowMaker = fLowMaker;
            this.cls = cls;
        }

        public IFlow<I,O> build() {

            return input -> {
                Object i = input;
                Object o = null;
                for (IFlow IFlow : flowMaker.flows) {
                    o = IFlow.run(i);
                    i = o;
                }
                return (O) o;
            };
        }


        <O1, TC> Builder<I,O,C,O1> next(Class<TC> targetClass, IProcess<T,O1,C> process) {
            this.flowMaker.flows.add(flowMap.get(targetClass).apply(process, flowMaker.context));
            Class<?>[] classes = TypeResolver.resolveRawArguments(targetClass, process.getClass());
            return new Builder<>(this.flowMaker, (Class<O1>) classes[1]);
        }

        <TC> Finisher<I,O,C> last(Class<TC> targetClass, IProcess<T,O,C> process) {
            this.flowMaker.flows.add(flowMap.get(targetClass).apply(process, flowMaker.context));
            Class<?>[] classes = TypeResolver.resolveRawArguments(targetClass, process.getClass());
            return new Finisher<>(this.flowMaker, (Class<O>) classes[1]);
        }



        //添加纯计算
//        <O1> Builder<I,O,C,O1> nextPure(IPureProcess<T,O1,C> pureProcess) {
//            this.flowMaker.flows.add((IFlow<T, O1>) input -> pureProcess.process(input, flowMaker.context));
//            Class<?>[] classes = TypeResolver.resolveRawArguments(IPureProcess.class, pureProcess.getClass());
//            return new Builder<>(this.flowMaker, (Class<O1>) classes[1]);
//        }
//
//        <O1> Builder<I,O,C,O1> nextYesNoBranch(IYesNoBranchProcess<T,O1,C> IYesNoBranchProcess) {
//            this.flowMaker.flows.add((IFlow<T,O1>) input -> {
//                if (IYesNoBranchProcess.isYes(input, flowMaker.context)) {
//                    return (O1) IYesNoBranchProcess.yes(flowMaker.context).run(input);
//                }
//                return (O1) IYesNoBranchProcess.no(flowMaker.context).run(input);
//            });
//            Class<?>[] classes = TypeResolver.resolveRawArguments(IYesNoBranchProcess.class, IYesNoBranchProcess.getClass());
//            return new Builder<>(this.flowMaker, (Class<O1>) classes[1]);
//        }

//        <O1,E1,E2> Builder<I,O,C,O1> nextParallel(IParallelProcess<T,O1,C,E1,E2> IParallelProcess) {
//            this.flowMaker.flows.add((IFlow<T,O1>) input -> {
//                Predicate<E1> predicate = e -> IParallelProcess.filter(e, flowMaker.context);
//                Function<E1,E2> mapper = e -> IParallelProcess.mapper(e, flowMaker.context);
//                return IParallelProcess.toList(input, flowMaker.context)
//                        .parallelStream()
//                        .filter(predicate)
//                        .map(mapper)
//                        .collect(IParallelProcess.collector(flowMaker.context));
//            });
//            Class<?>[] classes = TypeResolver.resolveRawArguments(IParallelProcess.class, IParallelProcess.getClass());
//            return new Builder<>(this.flowMaker, (Class<O1>) classes[1]);
//        }

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
