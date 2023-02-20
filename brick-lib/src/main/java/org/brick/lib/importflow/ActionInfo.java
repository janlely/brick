package org.brick.lib.importflow;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActionInfo<T> {

    private Integer type;
    private T actionData;
}
