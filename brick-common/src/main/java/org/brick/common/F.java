package org.brick.common;


import org.brick.common.types.Pair;

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
        return (a,__) -> function.apply(a);
    }

    /**
     * like (fst . second f) where f :: a -> b
     * @param function
     * @param <A>
     * @param <B>
     * @param <C>
     * @return
     */
    public static <A,B,C> BiFunction<A,B,C> second(Function<B,C> function) {
        return (__,b) -> function.apply(b);
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


    public static <L,R,T> Function<Pair<L,R>, T> uncurry(BiFunction<L,R,T> func) {
        return p -> func.apply(Pair.getLeft(p), Pair.getRight(p));
    }

    public static <A,B,C> Function<A,C> combo(Function<A,B> fst, Function<B,C> snd) {
        return a -> snd.apply(fst.apply(a));
    }

    public static <A,B,C,D> Function<A,D> combo(Function<A,B> fst, Function<B,C> snd, Function<C,D> trd) {
        return a -> trd.apply(snd.apply(fst.apply(a)));
    }

    public static <A,B,C,D,E> Function<A,E> combo(Function<A,B> fst, Function<B,C> snd, Function<C,D> trd, Function<D,E> fth) {
        return a -> fth.apply(trd.apply(snd.apply(fst.apply(a))));
    }
}
