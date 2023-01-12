package org.brick;

public interface PureProcess<I,O,C> {
    O process(I input, C context);
}
