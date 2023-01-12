package org.brick;

public class FlowHelper {

    public static <I,O,C> Flow<I,O> fromPure(PureProcess<I,O,C> process, C context) {
        return new Flow<I, O>() {
            @Override
            public O run(I input) {
                return process.process(input, context);
            }
        };
    }
}
