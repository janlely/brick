package org.brick.springboot.demo;

import org.apache.commons.lang3.StringUtils;
import org.brick.springboot.demo.flows.HelloWorldFlow;
import org.brick.types.Pair;
import org.brick.unit.FlowTester;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HelloWorldTest {

    @Autowired
    private HelloWorldFlow helloWorldFlow;
    @Test
    public void testHelloWorld() {
        HelloWorldFlow.HelloContext context = new HelloWorldFlow.HelloContext();
        HelloWorldFlow.HelloRequest req = new HelloWorldFlow.HelloRequest();
        req.setName("jay");
        assert new FlowTester<Void, String, Pair<HelloWorldFlow.HelloRequest, HelloWorldFlow.HelloContext>>()
                .linkUnit(helloWorldFlow.getFirstStep())
                .pass(s -> StringUtils.equals(s, "jay"))
                .build()
                .run(null, new Pair(req, context));

        assert new FlowTester<Void, String, Pair<HelloWorldFlow.HelloRequest, HelloWorldFlow.HelloContext>>()
                .linkUnit(helloWorldFlow.getFirstStep())
                .linkUnit(helloWorldFlow.getSecondStep())
                .pass(s -> StringUtils.equals(s, "yaj"))
                .build().run(null, new Pair<>(req, context));
    }
}
