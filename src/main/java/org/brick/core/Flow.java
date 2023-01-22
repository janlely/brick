package org.brick.core;

public interface Flow<I,O,C>  {

    FlowDoc<I,O,C> getFlowDoc();

    String getFlowName();

    O run(final I input, C context);

    default boolean isEnd() {
        return false;
    }

    default boolean isAsync() {
        return false;
    }

}
