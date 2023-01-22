package org.brick.lib.importflow;

import org.brick.core.AbortWhenFlow;
import org.brick.core.Flow;
import org.brick.core.FlowHelper;
import org.brick.core.FlowMaker;
import org.brick.core.LoopFlow;
import org.brick.core.ModifyContext;
import org.brick.core.ModifyInputFlow;
import org.brick.core.PureFunction;
import org.brick.lib.IFlow;
import org.brick.types.Either;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 *
 * @param <ERR> type of error
 * @param <E> type of element
 * @param <S> type of supporting data
 * @param <O1> type of output of each chunk
 * @param <O> type of output
 * @param <UE> type of user defined env
 * @param <UC> type of user defined context
 */
public interface IImportFlow<ERR,E,S,O1,O, UE, UC> extends IFlow<ImportEnv<ERR,E,S, UE,O1>,O,ImportContext<UC>> {

    /**
     * parse raw InputStream to List of T
     * @param inputStream
     * @return
     */
    Either<Exception, List<E>> parseData(InputStream inputStream, ImportContext<UC> context);

    /**
     * check data validity base on data self
     * @param input
     * @param elem
     * @param context
     * @return error if exist
     */
    Optional<ERR> preCheck(ImportEnv<ERR,E,S, UE,O1> input, E elem, ImportContext<UC> context);

    /**
     * check data validity base on supporting data
     * @param input
     * @param elem
     * @param context
     * @return error if exist
     */
    Optional<ERR> postCheck(ImportEnv<ERR,E,S, UE,O1> input, E elem, ImportContext<UC> context);


    /**
     * handler error
     * @param input
     * @param context
     * @return
     */
    O1 handlerError(ImportEnv<ERR,E,S, UE,O1> input, ImportContext<UC> context);

    /**
     * Combinators
     * @return
     */
    ActionCombinators getCombinators();

    /**
     * ActionExecutor
     * @return
     */
    ActionExecutor getActionExecutor();

    /**
     * prepare action related
     * @param input
     * @param elem
     * @param context
     * @return
     */
    List<ActionInfo> toPrepareActions(ImportEnv<ERR,E,S, UE,O1> input, E elem, ImportContext<UC> context);
    boolean ifAbortAfterPrepared(ImportEnv<ERR,E,S, UE,O1> input, ImportContext<UC> context);
    O1 abortAfterPrepared(ImportEnv<ERR,E,S, UE,O1> input, ImportContext<UC> context);
    S collect(ImportEnv<ERR,E,S, UE,O1> input, ImportContext<UC> context);

    /**
     * final action related
     * @param input
     * @param elem
     * @param context
     * @return
     */
    List<ActionInfo> toFinalActions(ImportEnv<ERR,E,S, UE,O1> input, E elem, ImportContext<UC> context);
    O1 toFinalResponse(ImportEnv<ERR,E,S, UE,O1> input, ImportContext<UC> context);


    /**
     * do something before/after
     * @param context
     */
    ImportContext<UC> before(ImportContext<UC> context);
    ImportContext<UC> after(ImportContext<UC> context);


    /**
     * collect list of O1 to final O
     * @param context
     * @return
     */
    Collector<O1,?,O> getCollector(ImportContext<UC> context);

    /**
     * get a empty O1
     * @return O1
     */
    O1 empty();

    default void handlerParseException(Exception e) {
        System.out.println(e.getMessage());
//        throw new RuntimeException(e);
    }

    default Flow<ImportEnv<ERR,E,S, UE,O1>, O, ImportContext<UC>> getFlow() {
        Flow<ImportEnv<ERR,E,S, UE,O1>, O1, ImportContext<UC>> chunkFlow = new FlowMaker<ImportEnv<ERR,E,S, UE,O1>, O1, ImportContext<UC>>("flow for every chunk")
                .flowBuilder()
                .effect(new ModifyInputFlow<>("call parseData which will modify ImportEnv",
                        (i, c) -> {
                            Either<Exception,List<E>> chunk = parseData(i.getIns(), c);
                            if (Either.isLeft(chunk)) {
                                handlerParseException(Either.getLeft(chunk));
                            }
                            if (Either.isLeft(chunk) || Either.getRight(chunk) == null || Either.getRight(chunk).isEmpty()) {
                                i.setNoMoreDataToParse(true);
                                return;
                            }
//                            if (c.getConfig().isTotallyChunked()) {
                            i.setElements(Either.getRight(chunk));
//                            }else {
//                                i.addElements(Either.getRight(chunk));
//                            }
                        }))
                .abort(new AbortWhenFlow<>("abort when no data parsed", (i,c) -> i.isNoMoreDataToParse(),
                        FlowHelper.fromPure(new PureFunction<>("no more data to parse,just return empty", (i,c) -> empty()))))
                .effect(new ModifyInputFlow<>("check data validity",
                        (i,c) -> i.setErrors(i.getElements().parallelStream()
                                .map(e -> preCheck(i, e, c))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toList()))))
                .abort(new AbortWhenFlow<>("abort chunk processing if preCheck failed",
                        (i,c) -> i.getErrors() != null && !i.getErrors().isEmpty(),
                        FlowHelper.fromPure(new PureFunction<>("to response", (i,c) -> handlerError(i,c)))))
                .effect(new ModifyInputFlow<>("produce prepareActions for supporting data",
                        (i,c) -> i.setPrepareActions(i.getElements().stream().flatMap(e -> toPrepareActions(i, e, c).stream())
                                .collect(Collectors.groupingBy(ActionInfo::getType))
                                .entrySet()
                                .stream()
                                .flatMap(entry -> getCombinators().getCombinator(entry.getKey()).combine(entry.getValue()).stream())
                                .collect(Collectors.toList()))))
                .effect(new ModifyInputFlow<>("execute prepareActions",
                        (i,c) -> {
                            if (c.getConfig().isIfPrepareParallel()) {
                                i.setPrepareActionResponses(i.getPrepareActions().stream().map(info -> getActionExecutor().execute(info)).collect(Collectors.toList()));
                            }else {
                                i.setPrepareActionResponses(c.getConfig().getPrepareForkJoin().submit(() -> i.getPrepareActions().parallelStream().map(info -> getActionExecutor().execute(info)).collect(Collectors.toList())).join());
                            }
                        }))
                .abort(new AbortWhenFlow<>("abort after prepareActions executed",
                        (i,c) -> ifAbortAfterPrepared(i,c),
                        FlowHelper.fromPure(new PureFunction<>("do abort after prepare action response",
                                (i,c) -> abortAfterPrepared(i,c)))))
                .effect(new ModifyInputFlow<>("collect prepare action responses to supporting data",
                        (i,c) -> i.setSupportData(collect(i,c))))
                .effect(new ModifyInputFlow<>("do postCheck",
                        (i,c) -> i.setErrors(i.getElements().parallelStream()
                                .map(e -> postCheck(i, e, c))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toList()))))
                .abort(new AbortWhenFlow<>("abort importing if postCheck failed",
                        (i,c) -> i.getErrors() != null && !i.getErrors().isEmpty(),
                        FlowHelper.fromPure(new PureFunction<>("do abort after prepare action response",
                                (i,c) -> handlerError(i,c)))))
                .effect(new ModifyInputFlow<>("produce finalActions",
                        (i,c) -> i.setFinaActions(i.getElements().stream()
                                .flatMap(e -> toFinalActions(i,e,c).stream())
                                .collect(Collectors.groupingBy(ActionInfo::getType))
                                .entrySet().stream()
                                .flatMap(entry -> getCombinators().getCombinator(entry.getKey()).combine(entry.getValue()).stream())
                                .collect(Collectors.toList()))))
                .effect(new ModifyInputFlow<>("execute finalActions",
                        (i,c) -> {
                            if (c.getConfig().isIfFinalParallel()) {
                                i.setFinalActionResponses(i.getFinalActions().stream().map(info -> getActionExecutor().execute(info)).collect(Collectors.toList()));
                            }else {
                                i.setFinalActionResponses(c.getConfig().getFinalForkJoin().submit(() -> i.getFinalActions().stream().map(info -> getActionExecutor().execute(info)).collect(Collectors.toList())).join());
                            }
                        }))
                .pure(new PureFunction<>("collect response",
                        (i,c) -> toFinalResponse(i,c)))
                .build(null, null, null);

        return new FlowMaker<ImportEnv<ERR,E,S, UE,O1>, O, ImportContext<UC>>("Main flow of importing date from anything")
                .flowBuilder()
                .local(new ModifyContext<>("call before function which may produce side effects", this::before))
                .loop(new LoopFlow<>("handler input chunk by chunk", (i, c) -> c.getConfig().isQuickAbort()
                        ? i.isNoMoreDataToParse() || !i.getErrors().isEmpty()
                        : i.isNoMoreDataToParse(), chunkFlow, c -> getCollector(c)))
                .local(new ModifyContext<>("call before function which may produce side effects", this::after))
                .build(null, null, null);
    }
}
