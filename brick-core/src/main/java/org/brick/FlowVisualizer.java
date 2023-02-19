package org.brick;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FlowVisualizer {

    public static String toJson(Flow flow) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(flow.getFlowDoc());
    }

}
