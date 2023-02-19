package org.brick.lib.importf;

import lombok.Data;

import java.util.List;

@Data
public class ImportConText<UC,E,S> {

    private UC userContext;
    private Config config;
    private TempData<E,S> temp;

    @Data
    public static class Config {
        private int chunkSize;
        private boolean prepareActionParallel;
        private boolean finalActionParallel;
    }

    @Data
    public static class TempData<E,S> {
        List<E> elems;
        S supportingData;
    }

}
