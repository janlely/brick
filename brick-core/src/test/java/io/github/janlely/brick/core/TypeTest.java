package io.github.janlely.brick.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TypeTest {

    @Test
    public void testType() {
        FlowMaker<String, Integer, Boolean> flowmaker = new FlowMaker<String, Integer, Boolean>("test flow maker");
//        Class<?>[] res = TypeResolver.resolveRawArguments(FlowMaker.class, flowmaker.getClass());
        FlowMaker.Builder<String, Integer, Boolean, String> builder = flowmaker.flowBuilder();
        FlowMaker.Builder<String, Integer, Boolean, Integer> builder1 = builder.pure(new PureFunction<>("test pure", (i, c) -> Integer.parseInt(i)));
        List<Integer> lists = new ArrayList<>();
        System.out.println("hello");
    }

}
