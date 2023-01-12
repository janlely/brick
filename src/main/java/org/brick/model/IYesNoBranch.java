package org.brick.model;

import org.brick.core.IFlow;
import org.brick.core.IYesNoBranchFlow;

import java.util.function.BiFunction;
import java.util.function.Function;

public class IYesNoBranch<I,O,C> implements IYesNoBranchFlow<I,O,C> {

	private BiFunction<I,C,Boolean> condChecker;
	private Function<C, IFlow<I,O,C>> yesFlow;
	private Function<C, IFlow<I,O,C>> noFlow;

	public IYesNoBranch(BiFunction<I,C,Boolean> condChecker, Function<C, IFlow<I,O,C>> yesFlow,
						Function<C, IFlow<I,O,C>> noFlow) {
		this.condChecker = condChecker;
		this.yesFlow = yesFlow;
		this.noFlow = noFlow;
	}

	@Override
	public boolean isYes(I input, C context) {
		return this.condChecker.apply(input, context);
	}

	@Override
	public IFlow<I,O,C> yes(C context) {
		return this.yesFlow.apply(context);
	}

	@Override
	public IFlow<I,O,C> no(C context) {
		return this.noFlow.apply(context);
	}
	
}
