package org.brick;

import lombok.SneakyThrows;
import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collector;

public class CountDownFlow<I,O,O1,C> implements Flow<I,O,C> {

    private String desc;
    private int count;
    private List<Flow<I,O1,C>> flows;
    private Collector<O1,?,O> collector;
    private ExecutorService executorService;
    public CountDownFlow(String desc, ExecutorService executorService,
                         int count, Collector<O1,?,O> collector, Flow<I,O1,C> ...flows) {
        this.desc = desc;
        this.executorService = executorService;
        this.count = count;
        this.collector = collector;
        this.flows = new ArrayList<>();
        for (Flow<I, O1, C> flow : flows) {
            this.flows.add(flow);
        }
        if (this.flows.size() != this.count) {
            throw new RuntimeException("flow size does not match the count value");
        }
    }
    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I, O, C> flowDoc = new FlowDoc<>(this.desc, FlowType.COUNT_DOWN, this.getFlowName());
        for (Flow<I, O1, C> flow : this.flows) {
            flowDoc.innerFlowDocs.add(flow.getFlowDoc());
        }
        return flowDoc;
    }

    @Override
    public String getFlowName() {
        return ClassUtils.getShortClassName(CountDownFlow.class);
    }

    @SneakyThrows
    @Override
    public O run(I input, C context) {
        CountDownLatch latch = new CountDownLatch(this.count);
        List<O1> results = new ArrayList<>();
        for (Flow<I, O1, C> flow : this.flows) {
            this.executorService.submit(() -> {
                results.add(flow.run(input, context));
                latch.countDown();
            });
        }
        latch.wait();
        return results.stream().collect(this.collector);
    }
}
