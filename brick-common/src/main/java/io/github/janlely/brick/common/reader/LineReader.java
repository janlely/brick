package io.github.janlely.brick.common.reader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.IllegalFormatException;
import java.util.stream.Stream;

/**
 * @param <T> convert a InputStream to Stream of lines
 */
public class LineReader<T> implements StreamReader<T> {

    /**
     * the convertor
     */
    private Convertor<T> convertor;

    /**
     * @param convertor the convertor
     */
    public LineReader(Convertor convertor) {
        this.convertor = convertor;
    }

    @Override
    public Stream<T> read(InputStream is) {
        return new BufferedReader(new InputStreamReader(is)).lines()
                .map(convertor::convert);
    }

    /**
     * @param <T> the convertor interface
     */
    public interface Convertor<T> {
        /**
         * @param src the source string
         * @return the target type
         * @throws IllegalFormatException
         */
        T convert(String src) throws IllegalFormatException;
    }
}
