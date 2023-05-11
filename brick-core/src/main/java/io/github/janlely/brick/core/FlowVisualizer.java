package io.github.janlely.brick.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * the flow visualizer
 */
public class FlowVisualizer {

    /**
     * @param flow the flow
     * @return the json format result of flow
     * @throws JsonProcessingException
     */
    public static String toJson(Flow flow) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(flow.getFlowDoc());
    }

}
