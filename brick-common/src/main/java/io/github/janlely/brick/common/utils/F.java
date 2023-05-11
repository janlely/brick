package io.github.janlely.brick.common.utils;


import io.github.janlely.brick.common.types.Pair;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * the function util
 */
public class F {

    /**
     * equal to: const
     * @param output the output value
     * @param <I> the input type
     * @param <O> the output type
     * @return the output function
     */
    public  static <I,O> Function<I,O> constF(O output) {
        return __ -> output;
    }

    /**
     * like (const . const) in Haskell
     * c -&gt; (a -&gt; b -&gt; c)
     * @param output the output value
     * @param <I1> the input type
     * @param <I2> the input type
     * @param <O> the output type
     * @return the output BiFunction
     */
    public  static <I1,I2,O> BiFunction<I1,I2,O> constBi(O output) {
        return (i1,i2) -> output;
    }

    /**
     * equal to: flip . const
     * @param function the inner function
     * @param <A> the input type 1
     * @param <B> the input type 2
     * @param <C> the output type
     * @return the BiFunction
     */
    public static <A,B,C> BiFunction<A,B,C> first(Function<A,C> function) {
        return (a,__) -> function.apply(a);
    }

    /**
     * equal to: const
     * @param function the inner function
     * @param <A> the input type 1
     * @param <B> the input type 2
     * @param <C> the output type
     * @return the BiFunction
     */
    public static <A,B,C> BiFunction<A,B,C> second(Function<B,C> function) {
        return (__,b) -> function.apply(b);
    }

    /**
     * (a -&gt; Bool) -&gt; (a -&gt; Bool)
     * like (not . f) in Haskell
     * @param function the inner function
     * @param <A> the input type
     * @return the output function
     */
    public static <A> Function<A, Boolean> not(Function<A,Boolean> function) {
        return a -> !function.apply(a);
    }


    /**
     * equal to: uncurry :: (a -&gt; b -&gt; c) -&gt; (a,b) -&gt; c
     * @param func the inner BiFunction
     * @param <L> the left type
     * @param <R> the right type
     * @param <T> the target type
     * @return the output function
     */
    public static <L,R,T> Function<Pair<L,R>, T> uncurry(BiFunction<L,R,T> func) {
        return p -> func.apply(Pair.getLeft(p), Pair.getRight(p));
    }

    /**
     * equal to: .
     * @param fst the first function
     * @param snd the second function
     * @param <A> the input type 1
     * @param <B> the input type 2
     * @param <C> the output type
     * @return the output function
     */
    public static <A,B,C> Function<A,C> combo(Function<A,B> fst, Function<B,C> snd) {
        return a -> snd.apply(fst.apply(a));
    }

    /**
     * equal to: \f1 f2 f3 -&gt; f3 . f2 . f1
     * @param fst the first function
     * @param snd the second function
     * @param trd the third function
     * @param <A> the input type 1
     * @param <B> the input type 2
     * @param <C> the input type 3
     * @param <D> the output type
     * @return the output function
     */
    public static <A,B,C,D> Function<A,D> combo(Function<A,B> fst, Function<B,C> snd, Function<C,D> trd) {
        return a -> trd.apply(snd.apply(fst.apply(a)));
    }

    /**
     * equal to: \f1 f2 f3 f4 -&gt; f4 . f3 . f2 . f1
     * @param fst the first function
     * @param snd the second function
     * @param trd the third function
     * @param fth the fourth function
     * @param <A> the input type 1
     * @param <B> the input type 2
     * @param <C> the input type 3
     * @param <D> the input type 4
     * @param <E> the output type
     * @return the output function
     */
    public static <A,B,C,D,E> Function<A,E> combo(Function<A,B> fst, Function<B,C> snd, Function<C,D> trd, Function<D,E> fth) {
        return a -> fth.apply(trd.apply(snd.apply(fst.apply(a))));
    }

    /**
     * (a1 -&gt; b1 -&gt; c) -&gt; (a -&gt; a1) -&gt; (b -&gt; b1) -&gt; (a -&gt; b -&gt; c)
     * @param func the input BiFunction
     * @param func1 the first function
     * @param func2 the second function
     * @param <A> the input type 1
     * @param <A1> the new input type 1
     * @param <B> the input type 2
     * @param <B1> the new input type 2
     * @param <C> the output type
     * @return the output BiFunction
     */
    public static <A,A1,B,B1,C> BiFunction<A,B,C> bimap(BiFunction<A1,B1,C> func, Function<A,A1> func1, Function<B,B1> func2) {
        return (a,b) -> func.apply(func1.apply(a), func2.apply(b));
    }
}
