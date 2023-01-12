package org.brick;

import java.util.List;
import java.util.stream.Collector;

public interface IParallelProcess<I,O,C,E1,E2> extends IProcess<I,O,C> {

	List<E1> toList(I input, C context);

	boolean filter(E1 elem, C context);

	E2 mapper(E1 elem, C context);

	Collector<E2,?,O> collector(C context);

}
