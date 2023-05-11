package io.github.janlely.brick.common.reader;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * @param <T> convert InputStream to Stream&lt;T&gt;
 */
public interface StreamReader<T> {

    /**
     * @param is the InputStream
     * @return the target Stream
     */
    Stream<T> read(InputStream is);
}
