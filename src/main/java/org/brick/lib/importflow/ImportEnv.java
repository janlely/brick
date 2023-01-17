package org.brick.lib.importflow;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @param <ERR> type of error
 * @param <ELEM> type of element
 * @param <SUP> type of supporting data
 * @param <T> type of userDefined env
 */
public class ImportEnv<ERR,ELEM,SUP,T> {
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
    //user defined env
    @Getter
    @Setter
    private T userEnv;


    public ImportEnv(InputStream ins, T userEnv) {
        this.ins = ins;
        this.userEnv = userEnv;
    }

    public static class Progress {
        private ImportState state;
        private AtomicInteger current;
        private int total;
    }

    public ImportEnv<ERR,ELEM,SUP,T> setElements(List<ELEM> elements) {
        this.elements = elements;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP,T> setPrepareActions(List<ActionInfo> prepareActions) {
        this.prepareActions = prepareActions;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP,T> setPrepareActionResponses(List<ActionResponse> prepareActionResponses) {
        this.prepareActionResponses = prepareActionResponses;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP,T> setFinalActionResponses(List<ActionResponse> finalActionResponses) {
        this.finalActionResponses = finalActionResponses;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP,T> setSupportData(SUP supportData) {
        this.supportData = supportData;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP,T> setErrors(List<ERR> errors) {
        this.errors = errors;
        return this;
    }

    public ImportEnv<ERR,ELEM,SUP,T> setFinaActions(List<ActionInfo> finaActions) {
        this.finalActions = finaActions;
        return this;
    }

}
