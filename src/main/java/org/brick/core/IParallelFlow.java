package org.brick.core;

import org.brick.core.IPureFunction;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

public interface IParallelFlow<I,O,C,E1,E2> extends IPureFunction<I, O, C> {

	List<E1> toList(I input, C context);

	boolean filter(E1 elem, C context);

	E2 mapper(E1 elem, C context);

	Collector<E2,?,O> collector(C context);

	default O pureCalculate(final I input, C context) {
		Predicate<E1> predicate = e -> filter(e, context);
		Function<E1,E2> mapper = e -> mapper(e, context);
		return toList(input, context)
				.parallelStream()
				.filter(predicate)
				.map(mapper)
				.collect(collector(context));
	}

	@Override
	default String getFlowType() {
		return "IParallelFlow";
	}
}
