package org.brick.core;

import org.brick.core.Flow;
import org.brick.core.IFlow;
import org.brick.core.IPureProcess;

import java.util.function.Function;

public class FlowHelper {

    public static <I,O,C> Flow<I,O,C> fromPure(IPureProcess<I,O,C> process) {
        return (input, context) -> process.pureCalculate(input, context);
    }
}
