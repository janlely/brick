package org.brick.model;

import org.apache.commons.lang3.ClassUtils;
import org.brick.core.IPureFunction;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

public interface IParallelFlow<I,O,C,E1,E2> extends IPureFunction<I, O, C> {

	List<E1> toList(I input, C context);

	boolean filter1(E1 elem, C context);
	boolean filter2(E2 elem, C context);

	E2 mapper(E1 elem, C context);

	Collector<E2,?,O> collector(C context);

	default O pureCalculate(final I input, C context) {
		Predicate<E1> predicate1 = e -> filter1(e, context);
		Predicate<E2> predicate2 = e -> filter2(e, context);
		Function<E1,E2> mapper = e -> mapper(e, context);
		return toList(input, context)
				.parallelStream()
				.filter(predicate1)
				.map(mapper)
				.filter(predicate2)
				.collect(collector(context));
	}

	@Override
	default String getFlowType() {
		return IPureFunction.super.getFlowType() + ":" + ClassUtils.getShortClassName(IParallelFlow.class);
	}
}
