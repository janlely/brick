package org.brick.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public class F {

    /**
     * like const in Haskell
     * a -> b -> c
     * @param output
     * @param <I>
     * @param <O>
     * @return
     */
    public  static <I,O> Function<I,O> constFunction(O output) {
        return i -> output;
    }

    /**
     * like (const . const) in Haskell
     * c -> (a -> b -> c)
     * @param output
     * @param <I1>
     * @param <I2>
     * @param <O>
     * @return
     */
    public  static <I1,I2,O> BiFunction<I1,I2,O> constBiFunction(O output) {
        return (i1,i2) -> output;
    }

    /**
     * like (fst . first f) where f :: a -> b
     * @param function
     * @param <A>
     * @param <B>
     * @param <C>
     * @return
     */
    public static <A,B,C> BiFunction<A,B,C> first(Function<A,C> function) {
        return (a,b) -> function.apply(a);
    }

    /**
     * (a -> Bool) -> (a -> Bool)
     * like (not . f) in Haskell
     * @param function
     * @param <A>
     * @return
     */
    public static <A> Function<A, Boolean> not(Function<A,Boolean> function) {
        return a -> !function.apply(a);
    }


}
