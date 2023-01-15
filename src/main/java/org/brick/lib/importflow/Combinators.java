package org.brick.lib.importflow;

import java.util.HashMap;
import java.util.Map;

public class Combinators {

    private Map<Integer, Combinator> combinators = new HashMap<>();

    /**
     *
     * @param type: ActionInfo.type
     * @return
     */
    public Combinator getCombinator(Integer type) {
        return combinators.getOrDefault(type, a -> a);
    }

    private void registerCombinator(Integer type, Combinator combinator) {
        combinators.put(type, combinator);
    }
}
