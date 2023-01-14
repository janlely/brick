package org.brick.core;

public class SubFlow {

    protected interface ISubFlow<I,O,C> extends Flow<I,O,C>{

        @Override
        default String getFlowType() {
            return "ISubFlow";
        }
    }
}
