package org.brick;

import java.util.function.BiFunction;
import java.util.function.Function;

public class IYesNoBranch<I,O,C> implements IYesNoBranchProcess<I,O,C> {

	private BiFunction<I,C,Boolean> condChecker;
	private Function<C, IFlow<I,O>> yesFlow;
	private Function<C, IFlow<I,O>> noFlow;

	public IYesNoBranch(BiFunction<I,C,Boolean> condChecker, Function<C, IFlow<I,O>> yesFlow, Function<C, IFlow<I,O>> noFlow) {
		this.condChecker = condChecker;
		this.yesFlow = yesFlow;
		this.noFlow = noFlow;
	}

	@Override
	public boolean isYes(I input, C context) {
		return this.condChecker.apply(input, context);
	}

	@Override
	public IFlow<I, O> yes(C context) {
		return this.yesFlow.apply(context);
	}

	@Override
	public IFlow<I, O> no(C context) {
		return this.noFlow.apply(context);
	}
	
}
