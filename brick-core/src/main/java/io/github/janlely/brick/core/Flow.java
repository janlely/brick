package io.github.janlely.brick.core;

/**
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context
 */
public interface Flow<I,O,C>  {

    /**
     * @return the flow doc
     */
    FlowDoc<I,O,C> getFlowDoc();

    /**
     * @return the flow name
     */
    String getFlowName();

    /**
     * @param input the input
     * @param context the context
     * @return the output
     */
    O run(final I input, C context);

    /**
     * @return if is end
     */
    default boolean isEnd() {
        return false;
    }

    /**
     * @return if is async
     */
    default boolean isAsync() {
        return false;
    }

}
