package org.brick;

public class AsyncProc1 implements IAsyncFlow<String, String, Integer>{
    @Override
    public void async(String input, Integer context) {
        System.out.println("Async input: " + input);
    }
}
