package org.brick;

import lombok.Builder;
import lombok.Data;


public class DGraph<C> {

    private Node root;
    private Class<C> cls;

    public DGraph(Class<C> cls) {
        this.root = Node.builder()
                .build();
    }

    @Data
    @Builder
    public static class Node<I,O> {
        Node<?,I> parent;
        IFlow<I,O> IFlow;
    }
}
