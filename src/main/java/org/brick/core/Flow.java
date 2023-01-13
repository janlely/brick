package org.brick.core;

public interface Flow<I,O,C>  {

    FlowDoc<I,O,C> getFlowDoc();

    String getFlowType();

    O run(I input, C context);

    default boolean isAsync() {
        return false;
    }
}
