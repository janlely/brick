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
import org.brick.types.Either;
import org.brick.types.Pair;
import org.brick.util.FlowVisualizer;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collector;
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

        ImportContext<UserContext> context = ImportContext.<UserContext>builder()
                .config(ImportContext.Config.builder()
                        .ifFinalParallel(true)
                        .ifPrepareParallel(true)
                        .prepareForkJoin(new ForkJoinPool(1))
                        .finalForkJoin(new ForkJoinPool(1))
                        .build())
                .userContext(UserContext.builder()
                        .startTime(0)
                        .endTime(0)
                        .build())
                .build();
        Flow<ImportEnv<String, Element, FormatChecker, UserEnv, Result>, Result, ImportContext<UserContext>> importFlow = new SampleImport(actionExecutor, new ActionCombinators()).getFlow();
        Result result = importFlow.run(new ImportEnv<>(new ByteArrayInputStream(ins.getBytes(StandardCharsets.UTF_8)), userEnv), context);
        System.out.println(new ObjectMapper().writer().writeValueAsString(result));
        System.out.println(FlowVisualizer.toJson(importFlow));
    }


    public static class SampleImport implements IImportFlow<String, Element, FormatChecker, Result, Result, UserEnv, UserContext> {

        private ActionExecutor actionExecutor;
        private ActionCombinators actionCombinators;

        public SampleImport(ActionExecutor actionExecutor, ActionCombinators actionCombinators) {
            this.actionCombinators = actionCombinators;
            this.actionExecutor = actionExecutor;
        }

        @Override
        public Either<Exception, List<Element>> parseData(InputStream inputStream, ImportContext<UserContext> context) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return Either.right(objectMapper.readValue(inputStream, new TypeReference<>() {}));
            } catch (IOException e) {
                return Either.left(e);
            }
        }


        @Override
        public Optional<String> preCheck(ImportEnv<String, Element, FormatChecker, UserEnv, Result> input, Element elem, ImportContext<UserContext> context) {
            if (StringUtils.isBlank(elem.desc)) {
                return Optional.of("desc should not be empty");
            }
            return Optional.empty();
        }

        @Override
        public Optional<String> postCheck(ImportEnv<String, Element, FormatChecker, UserEnv, Result> input, Element elem, ImportContext<UserContext> context) {
            if (StringUtils.isBlank(elem.getValue())) {
                return Optional.of("value should not be empty");
            }
            return Optional.empty();
        }

        @Override
        public Result handlerError(ImportEnv<String, Element, FormatChecker, UserEnv, Result> input, ImportContext<UserContext> context) {
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
        public List<ActionInfo> toPrepareActions(ImportEnv<String, Element, FormatChecker, UserEnv, Result> input, Element elem, ImportContext<UserContext> context) {
            return List.of(ActionInfo.builder()
                            .type(1)
                            .actionData(elem)
                    .build());
        }

        @Override
        public boolean ifAbortAfterPrepared(ImportEnv<String, Element, FormatChecker, UserEnv, Result> input, ImportContext<UserContext> context) {
            return input.getPrepareActionResponses().size() != input.getElements().size();
        }

        @Override
        public Result abortAfterPrepared(ImportEnv<String, Element, FormatChecker, UserEnv, Result> input, ImportContext<UserContext> context) {
            return Result.builder()
                    .total(0)
                    .success(false)
                    .message("no enough prepare response")
                    .build();
        }

        @Override
        public FormatChecker collect(ImportEnv<String, Element, FormatChecker, UserEnv, Result> input, ImportContext<UserContext> context) {
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
        public List<ActionInfo> toFinalActions(ImportEnv<String, Element, FormatChecker, UserEnv, Result> input, Element elem, ImportContext<UserContext> context) {
            return List.of(ActionInfo.builder()
                    .type(2)
                    .actionData(elem)
                    .build());
        }


        @Override
        public Result toFinalResponse(ImportEnv<String, Element, FormatChecker, UserEnv, Result> input, ImportContext<UserContext> context) {
//            long cost = input.getUserEnv().getEndTime() - input.getUserEnv().getStartTime();
            return input.getFinalActionResponses().size() >= input.getElements().size()
                    ? Result.builder().total(input.getElements().size()).cost(0).success(true).build()
                    : Result.builder().total(input.getFinalActionResponses().size()).cost(0).success(false).message("partial success").build();
        }

        @Override
        public ImportContext<UserContext> before(ImportContext<UserContext> context) {
            context.getUserContext().setStartTime(System.currentTimeMillis());
            return context;
        }


        @Override
        public ImportContext<UserContext> after(ImportContext<UserContext> context) {
            context.getUserContext().setEndTime(System.currentTimeMillis());
            return context;
        }

        @Override
        public Collector<Result, ?, Result> getCollector(ImportContext<UserContext> context) {
            final long cost = context.getUserContext().getEndTime() - context.getUserContext().getStartTime();
            return Collectors.reducing(Result.builder().build(), Function.identity(),
                    (r1,r2) -> Result.builder()
                            .success(r1.success)
                            .total(r1.getTotal() + r2.getTotal())
                            .message(String.format("%s\n%s", r1.message, r2.message))
                            .cost(cost)
                            .build());
        }

        @Override
        public Result empty() {
            return new Result();
        }

        @Override
        public void handlerParseException(Exception e) {
            System.out.println(e.getMessage());
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserContext{
        long startTime;
        long endTime;
    }
}
