package io.github.janlely.brick.common;

import io.github.janlely.brick.common.utils.StreamUtil;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTest {

    @Test
    public void testChunk() {
        List<List<Integer>> list = StreamUtil.chunk(Stream.of(1, 2, 3, 4, 5, 6,7), 2).collect(Collectors.toList());
        assert list.size() == 4;
    }
}
