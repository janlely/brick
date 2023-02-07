package org.brick.lib;

import org.brick.Flow;

public interface IFlow<I,O,C> {

    Flow<I,O,C> getFlow();
}
