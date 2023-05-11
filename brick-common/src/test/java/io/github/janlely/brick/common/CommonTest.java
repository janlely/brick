package io.github.janlely.brick.common;

import io.github.janlely.brick.common.utils.F;
import org.junit.Test;

import java.util.function.BiFunction;

public class CommonTest {

    public static class User {

        public int getCode() {
            return 1;
        }
    }

    @Test
    public void testF() {
        BiFunction<User, User, Integer> f = F.bimap((a, b) -> a + b,
                F.combo(User::getCode, i -> i * 2),
                F.combo(User::getCode, i -> i * 3));
        Integer res = f.apply(new User(), new User());
        assert res == 5;
    }
}
