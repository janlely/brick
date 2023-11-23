package org.brick.springboot.demo.flows;

import io.github.janlely.brick.common.types.Pair;
import io.github.janlely.brick.core.Flow;
import io.github.janlely.brick.core.FlowMaker;
import io.github.janlely.brick.core.PureFunction;
import io.github.janlely.brick.core.UnitFunction;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    private Flow<Void, HelloResponse, Context> flow;

    @PostConstruct
    public void buildFlow() {
        this.flow = new FlowMaker<Void, HelloResponse, Context>("Main flow of hello world")
                .flowBuilder()
                .pure(new PureFunction<>("Get name from the HelloRequest", firstStep))
                .pure(new PureFunction<>("Reverse the name", secondStep))
                .pure(new PureFunction<>("Final: make response", finalStep))
                .build();
    }

    @Component
    public static class FirstStep implements UnitFunction<Void, String, Context> {

        @Override
        public String exec(Void input, Context context) {
            return context.getRequest().getName();
        }
    }

    @Component
    public static class SecondStep implements UnitFunction<String, String, Context> {

        @Override
        public String exec(String input, Context context) {
            return StringUtils.reverse(input);
        }
    }

    @Component
    public static class FinalStep implements UnitFunction<String, HelloResponse, Context> {

        @Override
        public HelloResponse exec(String input, Context context) {
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

    @Data
    public static class Context {

        private HelloRequest request;
        public Context(HelloRequest request) {
            this.request = request;
        }
    }
}
