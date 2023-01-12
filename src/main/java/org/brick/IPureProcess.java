package org.brick;

public interface IPureProcess<I,O,C> extends IProcess<I,O,C> {
    O process(I input, C context);
}
