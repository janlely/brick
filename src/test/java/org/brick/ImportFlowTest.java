package org.brick;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.brick.core.Flow;
import org.brick.lib.importflow.ActionCombinators;
import org.brick.lib.importflow.ActionExecutor;
import org.brick.lib.importflow.ActionInfo;
import org.brick.lib.importflow.ActionResponse;
import org.brick.lib.importflow.IImportFlow;
import org.brick.lib.importflow.ImportContext;
import org.brick.lib.importflow.ImportEnv;
import org.brick.types.Pair;
import org.brick.util.FlowVisualizer;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImportFlowTest {

    @SneakyThrows
    @Test
    public void testSampleImport() {
        ActionExecutor actionExecutor = new ActionExecutor();
        actionExecutor.registerAction(1, info -> {
            Element elem = (Element) info.getActionData();
            return new Pair<>(elem.getName(), elem.getValue());
        });

        actionExecutor.registerAction(2, info -> {
            Element elem = (Element) info.getActionData();
            //final action: save data or what ever
            return "data saved";
        });
        //language=TEXT
        String ins = "[{\"name\":\"jay\",\"desc\":\"hello, this is jay\", \"value\":\"hello jay\"},{\"name\":\"joy\",\"desc\":\"hello, this is joy\",\"value\":\"hello joy\"}]";
        UserEnv userEnv = UserEnv.builder().build();

        ImportContext<Object> context = ImportContext.builder()
                .config(ImportContext.Config.builder()
                        .ifFinalParallel(true)
                        .ifPrepareParallel(true)
                        .prepareForkJoin(new ForkJoinPool(1))
                        .finalForkJoin(new ForkJoinPool(1))
                        .build())
                .build();
        Flow<ImportEnv<String, Element, FormatChecker, UserEnv>, Result, ImportContext> importFlow = new SampleImport(actionExecutor, new ActionCombinators()).getFlow();
        Result result = importFlow.run(new ImportEnv<>(new ByteArrayInputStream(ins.getBytes(StandardCharsets.UTF_8)), userEnv), context);
        System.out.println(new ObjectMapper().writer().writeValueAsString(result));
        System.out.println(FlowVisualizer.toJson(importFlow));
    }


    public static class SampleImport implements IImportFlow<String, Element, FormatChecker, Result, UserEnv> {

        private ActionExecutor actionExecutor;
        private ActionCombinators actionCombinators;

        public SampleImport(ActionExecutor actionExecutor, ActionCombinators actionCombinators) {
            this.actionCombinators = actionCombinators;
            this.actionExecutor = actionExecutor;
        }

        @SneakyThrows
        @Override
        public List<Element> parseData(InputStream inputStream, ImportContext context) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        }

        @Override
        public Optional<String> preCheck(ImportEnv<String, Element, FormatChecker, UserEnv> input, Element elem, ImportContext context) {
            if (StringUtils.isBlank(elem.desc)) {
                return Optional.of("desc should not be empty");
            }
            return Optional.empty();
        }

        @Override
        public Optional<String> postCheck(ImportEnv<String, Element, FormatChecker, UserEnv> input, Element elem, ImportContext context) {
            if (StringUtils.isBlank(elem.getValue())) {
                return Optional.of("value should not be empty");
            }
            return Optional.empty();
        }

        @Override
        public Result handlerError(ImportEnv<String, Element, FormatChecker, UserEnv> input, ImportContext context) {
            return Result.builder()
                    .success(false)
                    .total(-1)
                    .build();
        }

        @Override
        public ActionCombinators getCombinators() {
            return this.actionCombinators;
        }

        @Override
        public ActionExecutor getActionExecutor() {
            return this.actionExecutor;
        }

        @Override
        public List<ActionInfo> toPrepareActions(ImportEnv<String, Element, FormatChecker, UserEnv> input, Element elem, ImportContext context) {
            return List.of(ActionInfo.builder()
                            .type(1)
                            .actionData(elem)
                    .build());
        }

        @Override
        public boolean ifAbortAfterPrepared(ImportEnv<String, Element, FormatChecker, UserEnv> input, ImportContext context) {
            return input.getPrepareActionResponses().size() != input.getElements().size();
        }

        @Override
        public Result abortAfterPrepared(ImportEnv<String, Element, FormatChecker, UserEnv> input, ImportContext context) {
            return Result.builder()
                    .total(0)
                    .success(false)
                    .message("no enough prepare response")
                    .build();
        }

        @Override
        public FormatChecker collect(ImportEnv<String, Element, FormatChecker, UserEnv> input, ImportContext context) {
            return FormatChecker.builder()
                    .checker(
                            input.getPrepareActionResponses().stream()
                                    .map(ActionResponse::getResponse)
                                    .map(Pair.class::cast)
                                    .collect(Collectors.toMap(p -> (String)Pair.getLeft(p), p -> (v -> v.matches((String)Pair.getRight(p)))))
                    )
                    .build();
        }

        @Override
        public List<ActionInfo> toFinalActions(ImportEnv<String, Element, FormatChecker, UserEnv> input, Element elem, ImportContext context) {
            return List.of(ActionInfo.builder()
                    .type(2)
                    .actionData(elem)
                    .build());
        }


        @Override
        public Result toFinalResponse(ImportEnv<String, Element, FormatChecker, UserEnv> input, ImportContext context) {
            long cost = input.getUserEnv().getEndTime() - input.getUserEnv().getStartTime();
            return input.getFinalActionResponses().size() >= input.getElements().size()
                    ? Result.builder().total(input.getElements().size()).cost(cost).success(true).build()
                    : Result.builder().total(input.getFinalActionResponses().size()).cost(cost).success(false).message("partial success").build();
        }

        @Override
        public void before(ImportEnv<String, Element, FormatChecker, UserEnv> input, ImportContext context) {
            input.getUserEnv().setStartTime(System.currentTimeMillis());
        }

        @Override
        public void after(ImportEnv<String, Element, FormatChecker, UserEnv> input, ImportContext context) {
            input.getUserEnv().setEndTime(System.currentTimeMillis());
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Element{
        String name;
        String desc;
        String value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        boolean success;
        int total;
        String message;
        long cost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormatChecker {
        private Map<String, Function<String, Boolean>> checker;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserEnv {
        long startTime;
        long endTime;
    }
}
