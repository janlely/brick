package org.brick.lib.importflow;

public enum ImportState {
    PRECHECKING(1),
    PREPARING(2),
    POSTCHECKING(3),
    IMPORTING(4),
    ERRORED(5),
    SUCCESS(6);

    int code;
    ImportState(int code) {
        this.code = code;
    }

}
