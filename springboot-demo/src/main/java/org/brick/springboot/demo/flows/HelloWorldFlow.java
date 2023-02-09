package org.brick.springboot.demo.flows;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.brick.Flow;
import org.brick.FlowMaker;
import org.brick.PureFunction;
import org.brick.UnitFunction;
import org.brick.types.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HelloWorldFlow {

    @Autowired
    @Getter
    private FirstStep firstStep;
    @Autowired
    @Getter
    private SecondStep secondStep;
    @Getter
    @Autowired
    private FinalStep finalStep;


    @Getter
    private final Flow<Void, HelloResponse, Pair<HelloRequest, HelloContext>> flow;

    public HelloWorldFlow() {
        this.flow = new FlowMaker<Void, HelloResponse, Pair<HelloRequest, HelloContext>>("Main flow of hello world")
                .flowBuilder()
                .pure(new PureFunction<>("Get name from the HelloRequest", firstStep))
                .pure(new PureFunction<>("Reverse the name", secondStep))
                .pure(new PureFunction<>("Final: make response", finalStep))
                .build();
    }

//    @PostConstruct
//    public void makeFlow() {
//    }

    @Component
    public static class FirstStep implements UnitFunction<Void, String, Pair<HelloRequest, HelloContext>> {

        @Override
        public String exec(Void input, Pair<HelloRequest, HelloContext> context) {
            return Pair.getLeft(context).getName();
        }
    }

    @Component
    public static class SecondStep implements UnitFunction<String, String, Pair<HelloRequest, HelloContext>> {

        @Override
        public String exec(String input, Pair<HelloRequest, HelloContext> context) {
            return StringUtils.reverse(input);
        }
    }

    @Component
    public static class FinalStep implements UnitFunction<String, HelloResponse, Pair<HelloRequest, HelloContext>> {

        @Override
        public HelloResponse exec(String input, Pair<HelloRequest, HelloContext> context) {
            return HelloResponse.builder()
                    .eman(input)
                    .build();
        }
    }

    @Data
    public static class HelloRequest {
        private String name;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HelloResponse {
        private String eman;
    }

    public static class HelloContext {

    }
}
