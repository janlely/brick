package org.brick.model;

import org.brick.core.IPureProcess;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

public interface IParallelFlow<I,O,C,E1,E2> extends IPureProcess<I, O, C> {

	List<E1> toList(I input, C context);

	boolean filter(E1 elem, C context);

	E2 mapper(E1 elem, C context);

	Collector<E2,?,O> collector(C context);

	default O pureCalculate(I input, C context) {
		Predicate<E1> predicate = e -> filter(e, context);
		Function<E1,E2> mapper = e -> mapper(e, context);
		return toList(input, context)
				.parallelStream()
				.filter(predicate)
				.map(mapper)
				.collect(collector(context));
	}
}
