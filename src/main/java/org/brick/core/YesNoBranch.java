package org.brick.core;

import net.jodah.typetools.TypeResolver;

import java.util.function.BiFunction;

public class YesNoBranch<I,O,C> implements IYesNoBranchFlow<I,O,C> {

	private BiFunction<I,C,Boolean> condChecker;
	private SubFlow.ISubFlow<I,O,C> yesFlow;
	private SubFlow.ISubFlow<I,O,C> noFlow;
	private String desc;

	public YesNoBranch(String desc, BiFunction<I,C,Boolean> condChecker, SubFlow.ISubFlow<I,O,C> yesFlow,
					   SubFlow.ISubFlow<I,O,C> noFlow) {
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
		return this.yesFlow;
	}

	@Override
	public SubFlow.ISubFlow<I, O, C> no() {
		return this.noFlow;
	}

	@Override
	public FlowDoc<I, O, C> getFlowDoc() {
		FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc);
		Class<?>[] classes = TypeResolver.resolveRawArguments(YesNoBranch.class, this.getClass());
		flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
		flowDoc.add(this.yesFlow.getFlowDoc());
		flowDoc.add(this.noFlow.getFlowDoc());
		return flowDoc;
	}
}
