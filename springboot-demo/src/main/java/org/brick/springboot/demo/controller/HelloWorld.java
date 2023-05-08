package org.brick.springboot.demo.controller;

import io.github.janlely.brick.common.types.Pair;
import org.brick.springboot.demo.flows.HelloWorldFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorld {


    @Autowired
    private HelloWorldFlow helloWorldFlow;

    @PostMapping(value = "/world", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HelloWorldFlow.HelloResponse hello(@RequestBody HelloWorldFlow.HelloRequest request) {
        return helloWorldFlow.getFlow().run(null, new Pair<>(request, new HelloWorldFlow.HelloContext()));
    }
}
