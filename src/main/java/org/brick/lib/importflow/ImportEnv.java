package org.brick.lib.importflow;

import lombok.Data;
import lombok.Getter;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @param <ERR> type of error
 * @param <ELEM> type of element
 * @param <SUP> type of supporting data
 */
public class ImportEnv<ERR,ELEM,SUP> {
    //InputStream of raw data
    @Getter
    private final InputStream ins;
    //list of errors during importing
    @Getter
    private List<ERR> errors;
    //list of parsed elements
    @Getter
    private List<ELEM> elements;
    //list of prepareAction for getting supporting data
    @Getter
    private List<ActionInfo> prepareActions;
    //list of prepareActionResponse after execution
    @Getter
    private List<ActionResponse> prepareActionResponses;
    //supporting data
    private SUP supportData;
    //list of finalAction
    @Getter
    private List<ActionInfo> finalActions;
    @Getter
    private List<ActionResponse> finalActionResponses;
    //id of the importing task
    private String id;
    //progress of the importing
    private Progress progress;

    public ImportEnv(InputStream ins) {
        this.ins = ins;
    }

    public static class Progress {
        private ImportState state;
        private AtomicInteger current;
        private int total;
    }

    public ImportEnv<ERR,ELEM,SUP> setElements(List<ELEM> elements) {
        this.elements = elements;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP> setPrepareActions(List<ActionInfo> prepareActions) {
        this.prepareActions = prepareActions;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP> setPrepareActionResponses(List<ActionResponse> prepareActionResponses) {
        this.prepareActionResponses = prepareActionResponses;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP> setFinalActionResponses(List<ActionResponse> finalActionResponses) {
        this.finalActionResponses = finalActionResponses;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP> setSupportData(SUP supportData) {
        this.supportData = supportData;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP> setErrors(List<ERR> errors) {
        this.errors = errors;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP> setFinaActions(List<ActionInfo> finaActions) {
        this.finalActions = finaActions;
        return this;
    }


}
