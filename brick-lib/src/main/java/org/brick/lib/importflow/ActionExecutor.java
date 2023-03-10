package org.brick.lib.importflow;

import java.util.HashMap;
import java.util.Map;

public class ActionExecutor {

    private Map<Integer, Action> actionMap = new HashMap<>();

    public ActionResponse execute(ActionInfo actionInfo) {
            if (!this.actionMap.containsKey(actionInfo.getType())) {
                throw new RuntimeException("unrecognized action");
            }
            Object response = this.actionMap.get(actionInfo.getType()).run(actionInfo);
            return ActionResponse.builder()
                    .info(actionInfo)
                    .response(response)
                    .build();

    }

    public void registerAction(Integer type, Action action) {
        this.actionMap.put(type, action);
    }


}
