package org.brick.lib.importflow;

import org.brick.Flow;
import org.brick.FlowMaker;
import org.brick.MapReduceFlow;
import org.brick.ModifyContext;
import org.brick.PureFunction;
import org.brick.ThrowWhenFlow;
import org.brick.common.utils.F;
import org.brick.common.utils.StreamUtil;
import org.brick.exception.ErrorHandler;
import org.brick.exception.FlowError;
import org.brick.lib.IFlow;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * abstraction of file import flow
 * @param <E> type of import Element
 * @param <O> type of output
 * @param <ER> type of error
 * @param <S> type of supporting data
 * @param <UC> type of user context
 */
public interface ImportFlow<E,O,ER,S,UC> extends IFlow<InputStream, O, ImportConText<UC,E,S>> {

    /**
     * read input stream, make it a Stream<E>
     * @param ins
     * @return
     */
    Stream<E> read(InputStream ins);

    /**
     * collect response of every chunk response
     * @param context
     * @return
     */
    Collector<O,?,O> chunkCollect(ImportConText<UC,E,S> context);

    /**
     * preCheck, you can check content format here
     * @param elem
     * @return
     */
    Optional<ER> preCheck(E elem);

    /**
     * produce actions to getting supporting data
     * @param elem
     * @param context
     * @return
     */
    List<ActionInfo> toPrepareAction(E elem, ImportConText<UC,E,S> context);

    /**
     * action combinators, you can combine actions with same type to lift efficiency
     * @return
     */
    ActionCombinators getCombinator();

    /**
     * action executor
     * @return
     */
    ActionExecutor getActionExecutor();

    /**
     * if you want to abort when prepare action failed
     * @param responses
     * @param conText
     * @return
     */
    boolean exitWhenPrepareActionFailed(List<ActionResponse> responses, ImportConText<UC,E,S> conText);

    /**
     * collect response of prepare action to supporting data
     * @param conText
     * @return
     */
    Collector<ActionResponse,?,S> supportDataCollect(ImportConText<UC,E,S> conText);

    /**
     * do post check if you want
     * @param elem
     * @param conText
     * @return
     */
    Optional<ER> postCheck(E elem, ImportConText<UC,E,S> conText);

    /**
     * produce final actions for import
     * @param elem
     * @param conText
     * @return
     */
    List<ActionInfo> toFinalActions(E elem, ImportConText<UC,E,S> conText);

    /**
     * collect response of final actions to O
     * @param conText
     * @return
     */
    Collector<ActionResponse,?,O> responseCollect(ImportConText<UC,E,S> conText);

    ErrorHandler<O> preCheckErrorHandler();
    ErrorHandler<O> postCheckErrorHandler();
    ErrorHandler<O> prepareActionErrorHandler();

    default Flow<InputStream, O, ImportConText<UC,E,S>> getFlow() {
        return new FlowMaker<InputStream, O, ImportConText<UC,E,S>>("main flow of importing data")
                .flowBuilder()
                .errorHandler(ErrorType.PRE_CHECK_ERROR.type, this.preCheckErrorHandler())
                .errorHandler(ErrorType.POST_CHECK_ERROR.type, this.postCheckErrorHandler())
                .errorHandler(ErrorType.PREPARE_ACTION_RAILED.type, this.prepareActionErrorHandler())
                .mapReduce(new MapReduceFlow<>(
                        "process by chunk",
                        F.bimap(StreamUtil::chunk,
                                this::read,
                                F.combo(ImportConText<UC, E, S>::getConfig, ImportConText.Config::getChunkSize)),
                        F.second(Function.identity()),
                        this::chunkCollect,
                        new FlowMaker<List<E>, O, ImportConText<UC, E, S>>("process of every chunk")
                                .flowBuilder()
                                .local(new ModifyContext<>("put chunk into context",
                                        (i, c) -> c.getTemp().setElems(i)))
                                .pure(new PureFunction<>("preCheck",
                                        (i, c) -> i.parallelStream().map(this::preCheck)
                                                .filter(Optional::isPresent)
                                                .map(Optional::get)
                                                .collect(Collectors.toList())))
                                .throwWhen(new ThrowWhenFlow<>("throw exception if preCheck has error",
                                        F.first(F.not(List::isEmpty)),
                                        (i, c) -> new FlowError(ErrorType.PRE_CHECK_ERROR.type, i)))
                                .pure(new PureFunction<>("produce prepareActions for supporting data",
                                        (__, c) -> c.getTemp().getElems()
                                                .parallelStream().flatMap(e -> toPrepareAction(e, c).stream())
                                                .collect(Collectors.groupingBy(ActionInfo::getType))
                                                .entrySet().stream()
                                                .flatMap(entry -> getCombinator().getCombinator(entry.getKey()).combine(entry.getValue()).stream())
                                                .collect(Collectors.toList())))
                                .pure(new PureFunction<>("execute prepareActions",
                                        (i, c) -> c.getConfig().isPrepareActionParallel()
                                                ? i.parallelStream().map(a -> getActionExecutor().execute(a)).collect(Collectors.toList())
                                                : i.stream().map(a -> getActionExecutor().execute(a)).collect(Collectors.toList())))
                                .throwWhen(new ThrowWhenFlow<>("throw exception when error happened",
                                        this::exitWhenPrepareActionFailed,
                                        (i, c) -> new FlowError(ErrorType.PREPARE_ACTION_RAILED.type, i)))
                                .pure(new PureFunction<>("collect supporting data",
                                        (i, c) -> i.parallelStream().collect(this.supportDataCollect(c))))
                                .local(new ModifyContext<>("put supporting data into context",
                                        (i, c) -> c.getTemp().setSupportingData(i)))
                                .pure(new PureFunction<>("post check",
                                        (__, c) -> c.getTemp().getElems()
                                                .parallelStream()
                                                .map(e -> postCheck(e, c))
                                                .filter(Optional::isPresent)
                                                .map(Optional::get)
                                                .collect(Collectors.toList())))
                                .throwWhen(new ThrowWhenFlow<>("throw exception when post check failed",
                                        F.first(F.not(List::isEmpty)),
                                        (i, c) -> new FlowError(ErrorType.POST_CHECK_ERROR.type, i)))
                                .pure(new PureFunction<>("produce finalActions",
                                        (__, c) -> c.getTemp().getElems().parallelStream()
                                                .flatMap(e -> toFinalActions(e, c).stream())
                                                .collect(Collectors.groupingBy(ActionInfo::getType))
                                                .entrySet().stream()
                                                .flatMap(entry -> getCombinator().getCombinator(entry.getKey())
                                                        .combine(entry.getValue()).stream()).collect(Collectors.toList())))
                                .pure(new PureFunction<>("execute final actions",
                                        (i, c) -> c.getConfig().isFinalActionParallel()
                                                ? i.parallelStream().map(a -> getActionExecutor().execute(a)).collect(Collectors.toList())
                                                : i.stream().map(a -> getActionExecutor().execute(a)).collect(Collectors.toList())))
                                .pure(new PureFunction<>("collect response",
                                        (i, c) -> i.parallelStream().collect(responseCollect(c))))
                                .build()))
                .build();
    }

}
