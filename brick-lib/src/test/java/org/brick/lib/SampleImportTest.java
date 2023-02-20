package org.brick.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.brick.Flow;
import org.brick.FlowVisualizer;
import org.brick.common.reader.LineReader;
import org.brick.common.types.Pair;
import org.brick.exception.ExceptionHandler;
import org.brick.lib.importflow.Action;
import org.brick.lib.importflow.ActionCombinators;
import org.brick.lib.importflow.ActionExecutor;
import org.brick.lib.importflow.ActionInfo;
import org.brick.lib.importflow.ActionResponse;
import org.brick.lib.importflow.ImportConText;
import org.brick.lib.importflow.ImportFlow;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SampleImportTest {

    @Test
    public void testGetResource() {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream("test.txt");
        List<String> res = new LineReader<String>(i -> i).read(ins).collect(Collectors.toList());
        System.out.println(res);
    }

    @SneakyThrows
    @Test
    public void testSampleImport() {

        ActionCombinators actionCombinators = new ActionCombinators();
        ActionExecutor actionExecutor = new ActionExecutor();

        actionExecutor.registerAction(1, (Action<Map<String,Long>, String>) info -> {
            String hexStr = Hex.encodeHexString(info.getActionData().getBytes());
            long id = Long.parseLong(hexStr, 16);
            return Map.of(info.getActionData(), id);
        });

        actionExecutor.registerAction(2, (Action<String, Pair<Elem, Support>>) info -> "success" );

        InputStream ins = this.getClass().getClassLoader().getResourceAsStream("test.txt");
        ImportConText<UserContext, Elem, Support> context = ImportConText.<UserContext, Elem, Support>builder()
                .config(ImportConText.Config.builder()
                        .chunkSize(1)
                        .finalActionParallel(true)
                        .prepareActionParallel(false)
                        .build())
                .temp(new ImportConText.TempData<>())
                .build();
        Flow<InputStream, Output, ImportConText<UserContext, Elem, Support>> flow = new SampleImportFlow(actionCombinators, actionExecutor).getFlow();
        String jsonFlow = FlowVisualizer.toJson(flow);
        System.out.println(jsonFlow);
        Output res = flow.run(ins, context);
        System.out.println(res);
    }


    public static class SampleImportFlow implements ImportFlow<Elem, Output, Error, Support, UserContext> {

        private ActionCombinators actionCombinators;
        private ActionExecutor actionExecutor;


        public SampleImportFlow(ActionCombinators actionCombinators, ActionExecutor actionExecutor) {
            this.actionCombinators = actionCombinators;
            this.actionExecutor = actionExecutor;
        }
        @Override
        public Stream<Elem> read(InputStream ins) {
            return new LineReader<Elem>(str -> {
                try {
                    return new ObjectMapper().reader().readValue(str, Elem.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).read(ins);
        }

        @Override
        public Collector<Output, ?, Output> chunkCollect(ImportConText<UserContext, Elem, Support> context) {
            return Collectors.reducing(new Output(), Function.identity(), (a,b) -> {
                a.setFailed(a.getFailed() + b.getFailed());
                a.setSucceed(a.getSucceed() + b.getSucceed());
                return a;
            });
        }

        @Override
        public Optional<Error> preCheck(Elem elem) {
            if (StringUtils.isBlank(elem.getName())) {
                return Optional.of(Error.builder()
                                .type(1)
                                .message("name should not be empty")
                        .build());
            }
            if (StringUtils.isBlank(elem.getValue())) {
                return Optional.of(Error.builder()
                        .type(1)
                        .message("value should not be empty")
                        .build());
            }
            if (elem.getType() <= 0) {
                return Optional.of(Error.builder()
                        .type(1)
                        .message("type should not greater then 0")
                        .build());
            }
            return Optional.empty();
        }

        @Override
        public List<ActionInfo> toPrepareAction(Elem elem, ImportConText<UserContext, Elem, Support> context) {
            ActionInfo info = ActionInfo.builder()
                    .type(1)
                    .actionData(elem.getName())
                    .build();
            return List.of(info);
        }

        @Override
        public ActionCombinators getCombinator() {
            return this.actionCombinators;
        }

        @Override
        public ActionExecutor getActionExecutor() {
            return this.actionExecutor;
        }

        @Override
        public boolean exitWhenPrepareActionFailed(List<ActionResponse> responses, ImportConText<UserContext, Elem, Support> conText) {
            return responses.stream().filter(a -> a.getResponse() == null).findAny().isPresent();
        }

        @Override
        public Collector<ActionResponse, ?, Support> supportDataCollect(ImportConText<UserContext, Elem, Support> conText) {
            return Collectors.reducing(new Support(), res -> {
                Support s = new Support();
                s.idMap.putAll((Map<String, Long>) (res.getResponse()));
                return s;
            }, (s1,s2) -> {
                s1.idMap.putAll(s2.idMap);
                return s1;
            });
        }

        @Override
        public Optional<Error> postCheck(Elem elem, ImportConText<UserContext, Elem, Support> conText) {
            if (!conText.getTemp().getSupportingData().idMap.containsKey(elem.getName())) {
                return Optional.of(Error.builder()
                                .type(2)
                                .message("id not found for " + elem.getName())
                        .build());
            }
            return Optional.empty();
        }

        @Override
        public List<ActionInfo> toFinalActions(Elem elem, ImportConText<UserContext, Elem, Support> conText) {
            ActionInfo info = ActionInfo.builder()
                    .type(2)
                    .actionData(new Pair(elem, conText.getTemp().getSupportingData()))
                    .build();
            return List.of(info);
        }

        @Override
        public Collector<ActionResponse, ?, Output> responseCollect(ImportConText<UserContext, Elem, Support> conText) {
            return Collectors.reducing(new Output(), a -> {
                Output o = new Output();
                if (a.getResponse() == null) {
                    o.setFailed(1);
                    return o;
                }
                o.setSucceed(1);
                return o;
            }, (a,b) -> {
                a.setFailed(a.getFailed() + b.getFailed());
                a.setSucceed(a.getSucceed() + b.getSucceed());
                return a;
            });
        }

        @Override
        public ExceptionHandler<Output> preCheckErrorHandler() {
            return content -> {
                List<Error> errors = (List<Error>) content;
                return Output.builder()
                        .failed(errors.size())
                        .build();
            };
        }

        @Override
        public ExceptionHandler<Output> postCheckErrorHandler() {
            return content -> {
                List<Error> errors = (List<Error>) content;
                return Output.builder()
                        .failed(errors.size())
                        .build();
            };
        }

        @Override
        public ExceptionHandler<Output> prepareActionErrorHandler() {
            return content -> {
                List<ActionResponse> responses = (List<ActionResponse>) content;
                return Output.builder()
                        .failed(Long.valueOf(responses.stream().filter(a -> a.getResponse() == null).count()).intValue())
                        .build();
            };
        }
    }

    @Data
    public static class Elem {

        private String name;
        private String value;
        private int type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Output {
        private int succeed;
        private int failed;
    }

    @Data
    @Builder
    public static class Error {
        private int type;
        private String message;
    }

    public static class Support {
        private Map<String, Long> idMap = new HashMap<>();
    }

    public static class UserContext {

    }


}
