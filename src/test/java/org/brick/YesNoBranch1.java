package org.brick;


import org.apache.commons.lang3.StringUtils;

public class YesNoBranch1 implements IYesNoBranchProcess<String, String, Integer> {

    @Override
    public boolean isYes(String input, Integer context) {
        return StringUtils.equals(input, "yes");
    }

    @Override
    public IFlow<String, String> yes(Integer context) {
        return FlowHelper.fromPure(new PureProc3(), context);
    }

    @Override
    public IFlow<String, String> no(Integer context) {
        return FlowHelper.fromPure(new PureProc4(), context);
    }
}
