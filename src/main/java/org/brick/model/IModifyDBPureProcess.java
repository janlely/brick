package org.brick.model;

public interface IModifyDBPureProcess<I,C> extends IModifyDBProcess<I,I,C> {

    void doDBModifyPure(I input, C context);

    default I doDBModify(I input, C context) {
        doDBModifyPure(input, context);
        return input;
    }

}
