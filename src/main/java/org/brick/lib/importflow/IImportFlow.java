package org.brick.lib.importflow;

import org.brick.core.AbortWhenFlow;
import org.brick.core.Flow;
import org.brick.core.FlowHelper;
import org.brick.core.FlowMaker;
import org.brick.core.ModifyInputFlow;
import org.brick.core.PureFunction;
import org.brick.core.SideEffect;
import org.brick.lib.IFlow;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @param <ERR> type of error
 * @param <E> type of element
 * @param <S> type of suppoting data
 * @param <O> type of output
 * @param <C> type of context
 */
public interface IImportFlow<ERR,E,S,O,C> extends IFlow<ImportEnv<ERR,E,S>,O,C> {

    /**
     * parse raw InputStream to List of T
     * @param inputStream
     * @return
     */
    List<E> parseData(InputStream inputStream, C context);

    /**
     * check data validity base on data self
     * @param input
     * @param elem
     * @param context
     * @return error if exist
     */
    Optional<ERR> preCheck(ImportEnv<ERR,E,S> input, E elem, C context);
    Optional<ERR> postCheck(ImportEnv<ERR,E,S> input, E elem, C context);


    /**
     * handler errord
     * @param input
     * @param context
     * @return
     */
    O handlerError(ImportEnv<ERR,E,S> input, C context);

    /**
     * Combinators
     * @return
     */
    Combinators getCombinators();

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
    List<ActionInfo> toPrepareActions(ImportEnv<ERR,E,S> input, E elem, C context);
    boolean ifAbortAfterPrepared(ImportEnv<ERR,E,S> input, C context);
    O abortAfterPrepared(ImportEnv<ERR,E,S> input, C context);
    S collect(ImportEnv<ERR,E,S> input, C context);

    /**
     * final action related
     * @param input
     * @param elem
     * @param context
     * @return
     */
    List<ActionInfo> toFinalActions(ImportEnv<ERR,E,S> input, E elem, C context);
    boolean ifAbortAfterFinal(ImportEnv<ERR,E,S> input, C context);
    O abortAfterFinal(ImportEnv<ERR,E,S> input, C context);
    O toFinalResponse(ImportEnv<ERR,E,S> input, C context);


    /**
     * do something before
     * @param input
     * @param context
     */
    void before(ImportEnv<ERR,E,S> input, C context);

    /**
     * do something after
     * @param input
     * @param context
     */
    void after(ImportEnv<ERR,E,S> input, C context);

    default Flow<ImportEnv<ERR,E,S>, O, C> getFlow() {
        return new FlowMaker<ImportEnv<ERR,E,S>, O, C>("Main flow of importing date from excel")
                .flowBuilder()
                .effect(new SideEffect<>("call before function which may produce side effects",
                        (i,c) -> {
                            before(i,c);
                            return i;
                        }))
                .effect(new ModifyInputFlow<>("call parseData which will modify ImportEnv",
                        (i,c) -> i.setElements(parseData(i.getIns(), c))))
                .effect(new ModifyInputFlow<>("check data validity",
                        (i,c) -> i.setErrors(i.getElements().parallelStream()
                                .map(e -> preCheck(i, e, c))
                                .filter(Optional::isPresent)
                                        .map(Optional::get)
                                .collect(Collectors.toList()))))
                .abort(new AbortWhenFlow<>("abort importing if preCheck failed",
                        (i,c) -> i.getErrors() != null && !i.getErrors().isEmpty(),
                        FlowHelper.fromEffect(new SideEffect<>("error handler after preCheck",
                                (i,c) -> handlerError(i, c)))))
                .effect(new SideEffect<>("produce prepareActions for supporting data",
                        (i,c) -> i.setPrepareActions(i.getElements().stream().flatMap(e -> toPrepareActions(i, e, c).stream())
                                .collect(Collectors.groupingBy(ActionInfo::getType))
                                .entrySet()
                                .stream()
                                .flatMap(entry -> getCombinators().getCombinator(entry.getKey()).combine(entry.getValue()).stream())
                                .collect(Collectors.toList()))))
                .effect(new SideEffect<>("execute prepareActions",
                        (i,c) -> i.setPrepareActionResponses(i.getPrepareActions().parallelStream()
                                        .map(info -> getActionExecutor().execute(info))
                                .collect(Collectors.toList()))))
                .abort(new AbortWhenFlow<>("abort after prepareActions executed",
                        (i,c) -> ifAbortAfterPrepared(i,c),
                        FlowHelper.fromEffect(new SideEffect<>("do abort after prepare action response",
                                (i,c) -> abortAfterPrepared(i,c)))))
                .effect(new SideEffect<>("collect prepare action responses to supporting data",
                        (i,c) -> i.setSupportData(collect(i,c))))
                .effect(new ModifyInputFlow<>("do postCheck",
                        (i,c) -> i.setErrors(i.getElements().parallelStream()
                                .map(e -> postCheck(i, e, c))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toList()))))
                .abort(new AbortWhenFlow<>("abort importing if preCheck failed",
                        (i,c) -> i.getErrors() != null && !i.getErrors().isEmpty(),
                        FlowHelper.fromEffect(new SideEffect<>("error handler after preCheck",
                                (i,c) -> handlerError(i, c)))))
                .effect(new SideEffect<>("produce finalActions",
                        (i,c) -> i.setFinaActions(i.getElements().stream()
                                .flatMap(e -> toFinalActions(i,e,c).stream())
                                .collect(Collectors.groupingBy(ActionInfo::getType))
                                .entrySet().stream()
                                .flatMap(entry -> getCombinators().getCombinator(entry.getKey()).combine(entry.getValue()).stream())
                                .collect(Collectors.toList()))))
                .effect(new SideEffect<>("execute finalActions",
                        (i,c) -> i.setFinalActionResponses(i.getFinalActions().parallelStream()
                                .map(info -> getActionExecutor().execute(info))
                                .collect(Collectors.toList()))))
                .abort(new AbortWhenFlow<>("abort after finalActions executed",
                        (i,c) -> ifAbortAfterFinal(i,c),
                        FlowHelper.fromEffect(new SideEffect<>("do abort after prepare action response",
                                (i,c) -> abortAfterFinal(i, c)))))
                .pure(new PureFunction<>("produce final response",
                        (i,c) -> toFinalResponse(i,c)))
                .finish()
                .build();
    }
}
