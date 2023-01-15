package org.brick.lib.importflow;

import org.brick.core.Flow;
import org.brick.core.FlowHelper;
import org.brick.core.FlowMaker;
import org.brick.core.PureFunction;
import org.brick.core.SideEffect;
import org.brick.core.YesNoBranch;
import org.brick.lib.IFlow;
import org.brick.types.Either;
import org.brick.util.F;

import java.util.List;
import java.util.Optional;

/**
 *
 * @param <S> Type of supporting data
 * @param <E> Type of error
 * @param <T> Type of data
 * @param <O> Type of output
 * @param <C> Type of context
 */
public interface IImportFlow<S,E,T,O,C> extends IFlow<byte[],O,C> {

    /**
     * parse raw data to List of T
     * @param data
     * @return
     */
    List<T> parseData(byte[] data);

    /**
     * check data validity base on data self
     * @param row
     * @return error if exist
     */
    Optional<E> preCheck(T row, C context);

    O handlerError(List<E> errors, C context);

    default Flow<byte[], O, C> getFlow() {
        return new FlowMaker<byte[],O,C>("Main flow of importing date from excel")
                .flowBuilder()
                .pure(new PureFunction<>("parse data from excel",
                        (i,c) -> parseData(i)))
                .pure(new PreCheckFlow<T,E,C>("check data validity",
                        (i,c) -> preCheck(i,c)))
                .subFlow(new YesNoBranch<Either<List<E>, List<T>>,O,C>(
                        F.first(Either::isLeft)),
                        FlowHelper.fromEffect(new SideEffect<>("Exit and handler error after preCheck",
                                (i,c) -> handlerError(i,c))),
                        new FlowMaker<>())
    }
}
