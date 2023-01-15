package org.brick.util;

import javax.swing.text.StyledEditorKit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class F {

    public  static <I,O> Function<I,O> constFunction(O output) {
        return i -> output;
    }

    public  static <I1,I2,O> BiFunction<I1,I2,O> constBiFunction(O output) {
        return (i1,i2) -> output;
    }

    public static <A,B,C> BiFunction<A,B,C> first(Function<A,C> function) {
        return (a,b) -> function.apply(a);
    }

    public static <A> Function<A, Boolean> not(Function<A,Boolean> function) {
        return a -> !function.apply(a);
    }

}
