package org.brick.lib.importflow;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.ForkJoinPool;

@Data
@Builder
public class ImportContext<T> {

    T userContext;
    Config config;

    @Data
    @Builder
    public static class Config{
        //execute prepare actions parallelly
        private boolean ifPrepareParallel;
        //execute final actions parallelly
        private boolean ifFinalParallel;
        //fork join pool of prepare actions
        private ForkJoinPool prepareForkJoin;
        //fork join pool of final actions
        private ForkJoinPool finalForkJoin;
        //abort quickly when error happens
        private boolean quickAbort;
        //processing between chunks are independent
        private boolean totallyChunked;
    }

}
