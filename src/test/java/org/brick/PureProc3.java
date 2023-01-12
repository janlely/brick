package org.brick;

import org.apache.commons.lang3.StringUtils;

public class PureProc3 implements PureProcess<String, String, Integer>{
    @Override
    public String process(String input, Integer context) {
        System.out.println("Proc3 input: " + input);
        System.out.println("Proc3 output: " + StringUtils.reverse(input));
        return StringUtils.reverse(input);
    }
}
