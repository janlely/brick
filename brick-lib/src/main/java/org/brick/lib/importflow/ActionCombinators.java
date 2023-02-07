package org.brick.lib.importflow;

import java.util.HashMap;
import java.util.Map;

public class ActionCombinators {

    private Map<Integer, ActionCombinator> combinators = new HashMap<>();

    /**
     *
     * @param type: ActionInfo.type
     * @return
     */
    public ActionCombinator getCombinator(Integer type) {
        return combinators.getOrDefault(type, a -> a);
    }

    private void registerCombinator(Integer type, ActionCombinator combinator) {
        combinators.put(type, combinator);
    }
}
