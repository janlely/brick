package org.brick.model;

import org.brick.core.Flow;
import org.brick.core.IFlow;
import org.brick.core.IPureProcess;
import org.brick.core.IYesNoBranchFlow;

import java.util.function.BiFunction;
import java.util.function.Function;

public class IYesNoBranch<I,O,C> implements IYesNoBranchFlow<I,O,C> {

	private BiFunction<I,C,Boolean> condChecker;
	private Flow<I,O,C> yesFlow;
	private Flow<I,O,C> noFlow;

	public IYesNoBranch(BiFunction<I,C,Boolean> condChecker, Flow<I,O,C> yesFlow,
						Flow<I,O,C> noFlow) {
		this.condChecker = condChecker;
		this.yesFlow = yesFlow;
		this.noFlow = noFlow;
	}

	@Override
	public IPureProcess<I, Boolean, C> isYes() {
		return (input, context) -> condChecker.apply(input, context);
	}

	@Override
	public Flow<I, O, C> yes() {
		return this.yesFlow;
	}

	@Override
	public Flow<I, O, C> no() {
		return this.noFlow;
	}
}
