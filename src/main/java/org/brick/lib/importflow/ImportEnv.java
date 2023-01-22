package org.brick.lib.importflow;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @param <ERR> type of error
 * @param <ELEM> type of element
 * @param <SUP> type of supporting data
 * @param <T> type of userDefined env
 * @param <O> type of response of each chunk
 */
public class ImportEnv<ERR,ELEM,SUP,T,O> {
    //InputStream of raw data
    @Getter
    private final InputStream ins;
    //list of errors during importing
    @Getter
    private List<ERR> errors;
    //list of parsed elements
    @Getter
    private List<ELEM> elements = new ArrayList<>();
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
    //no more data can be parsed from InputStream
    @Getter
    @Setter
    private boolean noMoreDataToParse;
    private List<O> chunkResponses = new ArrayList<>();
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

    public void addElements(List<ELEM> elements) {
        this.elements.addAll(elements);
    }

    public void setElements(List<ELEM> elements) {
        this.elements = elements;
    }

    public void setPrepareActions(List<ActionInfo> prepareActions) {
        this.prepareActions = prepareActions;
    }

    public void setPrepareActionResponses(List<ActionResponse> prepareActionResponses) {
        this.prepareActionResponses = prepareActionResponses;
    }

    public void setFinalActionResponses(List<ActionResponse> finalActionResponses) {
        this.finalActionResponses = finalActionResponses;
    }

    public void setSupportData(SUP supportData) {
        this.supportData = supportData;
    }

    public void setErrors(List<ERR> errors) {
        this.errors = errors;
    }

    public void setFinaActions(List<ActionInfo> finaActions) {
        this.finalActions = finaActions;
    }

    public void addFinalResponse(O response) {
        this.chunkResponses.add(response);
    }

}
