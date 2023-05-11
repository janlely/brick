package io.github.janlely.brick.core;

import lombok.SneakyThrows;
import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;

/**
 * CountDownLatch
 * @param <I> the input type
 * @param <O> the output type
 * @param <O1> the output type of each count flow
 * @param <C> the context
 */
public class CountDownFlow<I,O,O1,C> implements Flow<I,O,C> {

    /**
     * the description
     */
    private String desc;
    /**
     * how many counts
     */
    private int count;
    /**
     * flows
     */
    private List<Flow<I,O1,C>> flows;
    /**
     * result collector
     */
    private Collector<O1,?,O> collector;
    /**
     * the async executor service
     */
    private ExecutorService executorService;
    /**
     * the timeout
     */
    private long timeout;

    /**
     * @param desc the description
     * @param timeout the timeout
     * @param executorService the executor service
     * @param count the cout
     * @param collector the result collector
     * @param flows the flows
     */
    public CountDownFlow(String desc, long timeout, ExecutorService executorService,
                         int count, Collector<O1,?,O> collector, Flow<I,O1,C> ...flows) {

        assert count != 0;
        this.desc = desc;
        this.timeout = timeout;
        this.executorService = executorService;
        this.count = count;
        this.collector = collector;
        this.flows = new ArrayList<>();
        for (Flow<I, O1, C> flow : flows) {
            assert SubFlow.ISubFlow.class.isAssignableFrom(flow.getClass());
            this.flows.add(flow);
        }
        assert this.flows.size() == count;
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
        latch.await(timeout, TimeUnit.MILLISECONDS);
        return results.stream().collect(this.collector);
    }
}
