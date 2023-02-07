package org.brick.lib.importflow;

import java.util.List;

public interface ActionCombinator {

    List<ActionInfo>  combine(List<ActionInfo> actionInfos);
}
