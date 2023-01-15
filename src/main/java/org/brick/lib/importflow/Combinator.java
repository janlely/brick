package org.brick.lib.importflow;

import java.util.List;

public interface Combinator {

    List<ActionInfo>  combine(List<ActionInfo> actionInfos);
}
