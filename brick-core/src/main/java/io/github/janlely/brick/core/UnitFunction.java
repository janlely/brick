package io.github.janlely.brick.core;

/**
 * @param <I> the input type
 * @param <O> the output type
 * @param <C> the context type
 */
public interface UnitFunction<I,O,C> {

    /**
     * @param input the input
     * @param context the context
     * @return the output
     */
    O exec(I input, C context);
}
