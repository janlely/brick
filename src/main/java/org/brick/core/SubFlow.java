package org.brick.core;

import org.apache.commons.lang3.ClassUtils;

public class SubFlow {

    protected interface ISubFlow<I,O,C> extends Flow<I,O,C>{

        @Override
        default String getFlowName() {
            return ClassUtils.getShortClassName(SubFlow.class);
        }
    }
}
