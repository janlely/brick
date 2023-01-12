package org.brick;

import java.util.function.Function;

public class FlowHelper {

    public static <I,O,C> Function<C, IFlow<I,O>> fromPure(IPureProcess<I,O,C> process) {
        return context -> new IFlow<I, O>() {
            @Override
            public O run(I input) {
                return process.process(input, context);
            }
        };
    }
}
