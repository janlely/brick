package io.github.janlely.brick.common.reader;

import java.io.InputStream;
import java.util.stream.Stream;

public interface StreamReader<T> {

    Stream<T> read(InputStream is);
}
