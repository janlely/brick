package org.brick.lib.importflow;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActionInfo<I> {

    private Integer type;
    private I actionData;
}
