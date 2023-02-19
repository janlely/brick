package org.brick.lib.importf;

import lombok.Getter;

public enum ErrorType {
    PRE_CHECK_ERROR(1),
    PREPARE_ACTION_RAILED(2),
    POST_CHECK_ERROR(3);

    @Getter
    int type;

    ErrorType(int type) {
        this.type = type;
    }
}
