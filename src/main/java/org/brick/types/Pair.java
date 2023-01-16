package org.brick.types;

/**
 * like (,) in Haskell
 * @param <L>
 * @param <R>
 */
public class Pair<L,R> {

    private L left;
    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L,R> L getLeft(Pair<L,R> pair) {
        return pair.left;
    }

    public static <L,R> R getRight(Pair<L,R> pair) {
        return pair.right;
    }

}
