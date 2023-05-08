package io.github.janlely.brick.common;

import io.github.janlely.brick.common.utils.F;
import org.junit.Test;

public class CommonTest {

    public static class User {

        public int getCode() {
            return 1;
        }
    }

    @Test
    public void testF() {
        F.bimap((a,b) -> a + b,
                F.combo(User::getCode, i -> i * 2),
                F.combo(User::getCode, i -> i * 3)).apply(new User(), new User());
    }
}
