package org.brick.lib.importf;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActionInfo {

    private Integer type;
    private Object actionData;
}
