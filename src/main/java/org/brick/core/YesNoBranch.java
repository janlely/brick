package org.brick.core;

import org.apache.commons.lang3.ClassUtils;

import java.util.function.BiFunction;

public class YesNoBranch<I,O,C> implements IYesNoBranchFlow<I,O,C> {

	private BiFunction<I,C,Boolean> condChecker;
	private Flow<I,O,C> yesFlow;
	private Flow<I,O,C> noFlow;
	private String desc;

	public YesNoBranch(String desc, BiFunction<I,C,Boolean> condChecker, Flow<I,O,C> yesFlow,
					   Flow<I,O,C> noFlow) {
		assert SubFlow.ISubFlow.class.isAssignableFrom(yesFlow.getClass());
		assert SubFlow.ISubFlow.class.isAssignableFrom(noFlow.getClass());
		this.condChecker = condChecker;
		this.yesFlow = yesFlow;
		this.noFlow = noFlow;
		this.desc = desc;
	}

	@Override
	public BiFunction<I, C, Boolean> isYes() {
		return (input, context) -> condChecker.apply(input, context);
	}

	@Override
	public SubFlow.ISubFlow<I, O, C> yes() {
		return (SubFlow.ISubFlow<I, O, C>) this.yesFlow;
	}

	@Override
	public SubFlow.ISubFlow<I, O, C> no() {
		return (SubFlow.ISubFlow<I, O, C>) this.noFlow;
	}

	@Override
	public String getFlowName() {
		return IYesNoBranchFlow.super.getFlowName() + ":" + ClassUtils.getShortClassName(YesNoBranch.class);
	}

	@Override
	public FlowDoc<I, O, C> getFlowDoc() {
		FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, FlowType.BRANCH, this.getFlowName());
		flowDoc.add(this.yesFlow.getFlowDoc());
		flowDoc.add(this.noFlow.getFlowDoc());
		return flowDoc;
	}
}
