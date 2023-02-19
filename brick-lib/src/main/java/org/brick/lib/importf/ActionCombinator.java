package org.brick.lib.importf;

import java.util.List;

public interface ActionCombinator {

    List<ActionInfo>  combine(List<ActionInfo> actionInfos);
}
