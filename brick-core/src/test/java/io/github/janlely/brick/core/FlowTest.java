package io.github.janlely.brick.core;

import io.github.janlely.brick.common.types.Pair;
import io.github.janlely.brick.common.utils.F;
import io.github.janlely.brick.core.exception.FlowError;
import lombok.Builder;
import lombok.Data;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FlowTest {

    @Data
    @Builder
    public static class UserSession {
        private String username;
        private String token;
        private boolean toThrow;
    }

    @Data
    @Builder
    public static class UserInfo {
        private String username;
        private int userId;
    }
    @Test
    public void testFlow() {

        Flow<Void, String, UserSession> flow = new FlowMaker<Void, String, UserSession>("a sample api flow")
                .asyncExecutor(Executors.newSingleThreadExecutor())
                .flowBuilder()
                .onError(1, content -> {
                    System.out.println("got error: " + content);
                    return "errored";
                })
                .abort(new AbortWhenFlow<>("check token",
                        (__, context) -> context.getToken() != "hello world",
                        FlowHelper.fromPure(new PureFunction<>("return error message", (__, ___) -> "token wrong"))))
                .pure(new PureFunction<>("query user info", (__, context) -> UserInfo.builder().userId(1).username(context.getUsername()).build()))
                .effect(new SideEffect<>("do some effect", (userInfo, context) -> {
                    System.out.println("fetched user info: " + userInfo.getUserId());
                    return userInfo.getUserId();
                }))
                .throwWhen(new ThrowWhenFlow<>("throw error when something happenned",
                        (__, context) -> context.isToThrow(),
                        (__, context) -> new FlowError(1, "Something wrong")))
                .flow(new FlowMaker<Integer, List<Integer>, UserSession>("a sample sub flow")
                        .flowBuilder()
                        .pure(new PureFunction<>("to integers", (i, __) -> List.of(i, i + 1, i + 2, i + 3)))
                        .build())
                .foldl(new FoldlFlow<>("sum together", 0,
                        FlowHelper.fromPure(new PureFunction<>("do sum", (pair, context) -> Pair.getLeft(pair) + Pair.getRight(pair)))))
                .branch(new YesNoBranch<>("even or odd",
                        (i, __) -> i % 2 == 0,
                        FlowHelper.fromPure(new PureFunction<>("even branch", (i, context) -> String.format("%d", i))),
                        FlowHelper.fromPure(new PureFunction<>("odd branch", (i, context) -> String.format("%d", i)))))
                .pure(new PureFunction<>("extract int", (s, context) -> Integer.parseInt(s)))
                .mapReduce(new MapReduceFlow<Integer, Integer, UserSession, Integer, Integer, UserSession>("map-reduce sample",
                        (i, context) -> Stream.of(i, i + 1, i + 2, i + 3, i + 4),
                        F.second(Function.identity()),
                        context -> Collectors.reducing(0, i -> i, Integer::sum),
                        FlowHelper.fromPure(new PureFunction<>("indentify", (i, context) -> i))))
                .async(FlowHelper.fromPure(new PureFunction<>("to string", (i, context) -> String.valueOf(i))))
                .local(new ModifyContext<>("modify session", (i, context) -> context.setToken("hello")))
                .pure(new PureFunction<>("mod 3", (i,context) -> i % 3))
                .branch(new CaseBranch<>("sample case branch",
                        F.first(Function.identity()),
                        new CaseFlow<>(Set.of(0), FlowHelper.fromPure(new PureFunction<>("* 1", (i, context) -> 1 * i))),
                        new CaseFlow<>(Set.of(1), FlowHelper.fromPure(new PureFunction<>("* 2", (i, context) -> 2 * i))),
                        new CaseFlow<>(Set.of(2), FlowHelper.fromPure(new PureFunction<>("* 3", (i, context) -> 3 * i)))))
                .loop(new LoopFlow<>("sample loop", F.constBi("0"),
                        (i, __) -> i > 0,
                        (i, o, c) -> {
                            i--;
                            return i;
                        },
                        FlowHelper.fromPure(new PureFunction<>("to string", (i, context) -> String.valueOf(i)))))
                .build();

        String result1 = flow.run(null, UserSession.builder().token("hello world").username("jay").build());
        String result2 = flow.run(null, UserSession.builder().token("wrong token").username("jay").build());
        assert result1.equals("0");
        assert result2.equals("token wrong");
    }

}
