package io.github.janlely.brick.common.utils;


import io.github.janlely.brick.common.types.Pair;

import java.util.function.BiFunction;
import java.util.function.Function;

public class F {

    /**
     * equal to: const
     * @param output
     * @param <I>
     * @param <O>
     * @return
     */
    public  static <I,O> Function<I,O> constF(O output) {
        return __ -> output;
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
    public  static <I1,I2,O> BiFunction<I1,I2,O> constBi(O output) {
        return (i1,i2) -> output;
    }

    /**
     * equal to: flip . const
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
     * equal to: const
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


    /**
     * equal to: uncurry :: (a -> b -> c) -> (a,b) -> c
     * @param func
     * @return
     * @param <L>
     * @param <R>
     * @param <T>
     */
    public static <L,R,T> Function<Pair<L,R>, T> uncurry(BiFunction<L,R,T> func) {
        return p -> func.apply(Pair.getLeft(p), Pair.getRight(p));
    }

    /**
     * equal to: .
     * @param fst
     * @param snd
     * @return
     * @param <A>
     * @param <B>
     * @param <C>
     */
    public static <A,B,C> Function<A,C> combo(Function<A,B> fst, Function<B,C> snd) {
        return a -> snd.apply(fst.apply(a));
    }

    /**
     * equal to: \f1 f2 f3 -> f3 . f2 . f1
     * @param fst
     * @param snd
     * @param trd
     * @return
     * @param <A>
     * @param <B>
     * @param <C>
     * @param <D>
     */
    public static <A,B,C,D> Function<A,D> combo(Function<A,B> fst, Function<B,C> snd, Function<C,D> trd) {
        return a -> trd.apply(snd.apply(fst.apply(a)));
    }

    /**
     * equal to: \f1 f2 f3 f4 -> f4 . f3 . f2 . f1
     * @param fst
     * @param snd
     * @param trd
     * @param fth
     * @return
     * @param <A>
     * @param <B>
     * @param <C>
     * @param <D>
     * @param <E>
     */
    public static <A,B,C,D,E> Function<A,E> combo(Function<A,B> fst, Function<B,C> snd, Function<C,D> trd, Function<D,E> fth) {
        return a -> fth.apply(trd.apply(snd.apply(fst.apply(a))));
    }

    /**
     * (a1 -> b1 -> c) -> (a -> a1) -> (b -> b1) -> (a -> b -> c)
     * @param func
     * @param func1
     * @param func2
     * @return
     * @param <A>
     * @param <A1>
     * @param <B>
     * @param <B1>
     * @param <C>
     */
    public static <A,A1,B,B1,C> BiFunction<A,B,C> bimap(BiFunction<A1,B1,C> func, Function<A,A1> func1, Function<B,B1> func2) {
        return (a,b) -> func.apply(func1.apply(a), func2.apply(b));
    }
}
