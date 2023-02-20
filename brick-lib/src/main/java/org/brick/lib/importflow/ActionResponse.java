package org.brick.lib.importflow;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActionResponse<T> {
    private ActionInfo info;
    private T response;
}
