package org.brick.lib.importflow;

import org.brick.Flow;
import org.brick.FlowMaker;
import org.brick.MapReduceFlow;
import org.brick.ModifyContext;
import org.brick.PureFunction;
import org.brick.ThrowWhenFlow;
import org.brick.common.utils.F;
import org.brick.common.utils.StreamUtil;
import org.brick.exception.FlowException;
import org.brick.lib.IFlow;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ImportFlow<E,O,O1,ER,S,UC> extends IFlow<InputStream, O, ImportConText<UC,E,S>> {

    Stream<E> read(InputStream ins);

    Collector<O1,?,O> chunkCollect(ImportConText<UC,E,S> context);

    Optional<ER> preCheck(E elem);

    List<ActionInfo> toPrepareAction(E elem, ImportConText<UC,E,S> context);

    ActionCombinators getCombinator();

    ActionExecutor getActionExecutor();

    boolean exitWhenPrepareActionFailed(List<ActionResponse> responses, ImportConText<UC,E,S> conText);

    Collector<ActionResponse,?,S> supportDataCollect(ImportConText<UC,E,S> conText);

    Optional<ER> postCheck(E elem, ImportConText<UC,E,S> conText);

    List<ActionInfo> toFinalActions(E elem, ImportConText<UC,E,S> conText);

    Collector<ActionResponse,?,O1> responseCollect(ImportConText<UC,E,S> conText);

    default Flow<InputStream, O, ImportConText<UC,E,S>> getFlow() {
        return new FlowMaker<InputStream, O, ImportConText<UC,E,S>>("main flow of importing data")
                .flowBuilder()
                .mapReduce(new MapReduceFlow<InputStream, O, ImportConText<UC,E,S>, List<E>, O1, ImportConText<UC,E,S>>(
                        "process by chunk",
                        F.bimap(StreamUtil::chunk,
                                this::read,
                                F.combo(ImportConText<UC,E,S>::getConfig, ImportConText.Config::getChunkSize)),
                        F.second(Function.identity()),
                        this::chunkCollect,
                        new FlowMaker<List<E>, O1, ImportConText<UC,E,S>>("process of every chunk")
                                .flowBuilder()
                                .local(new ModifyContext<>("put chunk into context",
                                        (i,c) -> c.getTemp().setElems(i)))
                                .pure(new PureFunction<>("preCheck",
                                        (i,c) -> i.parallelStream().map(this::preCheck)
                                                .filter(Optional::isPresent)
                                                .map(Optional::get)
                                                .collect(Collectors.toList())))
                                .throwWhen(new ThrowWhenFlow<>("throw exception if preCheck has error",
                                        F.first(F.not(List::isEmpty)),
                                        (i,c) -> new FlowException(ErrorType.PRE_CHECK_ERROR.type, i)))
                                .pure(new PureFunction<>("produce prepareActions for supporting data",
                                        (__,c) -> c.getTemp().getElems()
                                                .parallelStream().flatMap(e -> toPrepareAction(e, c).stream())
                                                .collect(Collectors.groupingBy(ActionInfo::getType))
                                                .entrySet().stream()
                                                .flatMap(entry -> getCombinator().getCombinator(entry.getKey()).combine(entry.getValue()).stream())
                                                .collect(Collectors.toList())))
                                .pure(new PureFunction<>("execute prepareActions",
                                        (i,c) -> c.getConfig().isPrepareActionParallel()
                                                ? i.parallelStream().map(a -> getActionExecutor().execute(a)).collect(Collectors.toList())
                                                : i.stream().map(a -> getActionExecutor().execute(a)).collect(Collectors.toList())))
                                .throwWhen(new ThrowWhenFlow<>("throw exception when error happened",
                                        this::exitWhenPrepareActionFailed,
                                        (i,c) -> new FlowException(ErrorType.PREPARE_ACTION_RAILED.type, i)))
                                .pure(new PureFunction<>("collect supporting data",
                                        (i,c) -> i.parallelStream().collect(this.supportDataCollect(c))))
                                .local(new ModifyContext<>("put supporting data into context",
                                        (i,c) -> c.getTemp().setSupportingData(i)))
                                .pure(new PureFunction<>("post check",
                                        (__,c) -> c.getTemp().getElems()
                                                .parallelStream()
                                                .map(e -> postCheck(e,c))
                                                .filter(Optional::isPresent)
                                                .map(Optional::get)
                                                .collect(Collectors.toList())))
                                .throwWhen(new ThrowWhenFlow<>("throw exception when post check failed",
                                        F.first(F.not(List::isEmpty)),
                                        (i,c) -> new FlowException(ErrorType.POST_CHECK_ERROR.type, i)))
                                .pure(new PureFunction<>("produce finalActions",
                                        (__,c) -> c.getTemp().getElems().parallelStream()
                                                .flatMap(e -> toFinalActions(e,c).stream())
                                                .collect(Collectors.groupingBy(ActionInfo::getType))
                                                .entrySet().stream()
                                                .flatMap(entry -> getCombinator().getCombinator(entry.getKey())
                                                        .combine(entry.getValue()).stream()).collect(Collectors.toList())))
                                .pure(new PureFunction<>("execute final actions",
                                        (i,c) -> c.getConfig().isFinalActionParallel()
                                                ? i.parallelStream().map(a -> getActionExecutor().execute(a)).collect(Collectors.toList())
                                                : i.stream().map(a -> getActionExecutor().execute(a)).collect(Collectors.toList())))
                                .pure(new PureFunction<>("collect response",
                                        (i,c) -> i.parallelStream().collect(responseCollect(c))))
                                .build()))
                .build();
    }

}
