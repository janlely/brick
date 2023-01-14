package org.brick.core;

public interface IModifyDBPureFlow<I,C> extends IModifyDBFlow<I,I,C> {

    void doDBModifyPure(final I input, C context);

    default I doDBModify(final I input, C context) {
        doDBModifyPure(input, context);
        return input;
    }

}
