package org.brick;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.brick.Flow;

public class FlowVisualizer {

    public static String toJson(Flow flow) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(flow.getFlowDoc());
    }

}
