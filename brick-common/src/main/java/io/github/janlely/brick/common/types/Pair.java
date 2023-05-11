package io.github.janlely.brick.common.types;

/**
 * like (,) in Haskell
 * @param <L>
 * @param <R>
 */
public class Pair<L,R> {

    /**
     * the left value
     */
    private L left;
    /**
     * the right value
     */
    private R right;

    /**
     * @param left the left value
     * @param right the right value
     */
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * @param pair the pair
     * @param <L> the left type
     * @param <R> the right type
     * @return the left value
     */
    public static <L,R> L getLeft(Pair<L,R> pair) {
        return pair.left;
    }

    /**
     * @param pair the pair
     * @param <L> the left type
     * @param <R> the right type
     * @return the right value
     */
    public static <L,R> R getRight(Pair<L,R> pair) {
        return pair.right;
    }

}
