package org.brick.lib.importflow;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActionResponse {
    private Integer type;
    private Object response;
}
