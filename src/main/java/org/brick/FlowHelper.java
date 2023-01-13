package org.brick;

import org.brick.core.Flow;
import org.brick.core.IFlow;
import org.brick.core.IPureProcess;

import java.util.function.Function;

public class FlowHelper {

    public static <I,O,C> Function<C, Flow<I,O,C>> fromPure(IPureProcess<I,O,C> process) {
        return context -> new Flow<I,O,C>() {
            @Override
            public O run(I input, C context) {
                return process.pureCalculate(input, context);
            }
        };
    }
}
