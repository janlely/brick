package org.brick;

import org.apache.commons.lang3.StringUtils;
import org.brick.core.IPureProcess;

public class PureProc3 implements IPureProcess<String, String, Integer> {

    @Override
    public String pureCalculate(String input, Integer context) {
        System.out.println(String.format("Proc3 input: %s, context: %d", input, context));
        System.out.println(String.format("Proc3 output: ", StringUtils.reverse(input)));
        return StringUtils.reverse(input);
    }
}
