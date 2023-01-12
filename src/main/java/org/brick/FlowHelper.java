package org.brick;

import java.util.function.Function;

public class FlowHelper {

    public static <I,O,C> Function<C, IFlow<I,O,C>> fromPure(IPureProcess<I,O,C> process) {
        return context -> new IFlow<I,O,C>() {
            @Override
            public O run(I input, C context) {
                return process.pureCalculate(input, context);
            }
        };
    }
}
