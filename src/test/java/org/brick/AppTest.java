package org.brick;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        List<Container<Integer>> list = new ArrayList<>();
        list.add(new Container("hello"));
        list.add(new Container<Integer>(123));
        for (Container container : list) {
            System.out.println(container.elem);
        }
    }
}
