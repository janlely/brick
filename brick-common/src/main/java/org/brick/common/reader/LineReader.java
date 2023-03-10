package org.brick.common.reader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.IllegalFormatException;
import java.util.stream.Stream;

public class LineReader<T> implements StreamReader<T> {

    private Convertor<T> convertor;

    public LineReader(Convertor convertor) {
        this.convertor = convertor;
    }

    @Override
    public Stream<T> read(InputStream is) {
        return new BufferedReader(new InputStreamReader(is)).lines()
                .map(convertor::convert);
    }

    public interface Convertor<T> {
        T convert(String src) throws IllegalFormatException;
    }
}
