package io.github.janlely.brick.common.types;

/**
 * like Either in Haskell
 * @param <L>
 * @param <R>
 */
public class Either<L,R> {

    /**
     * the value
     */
    private Object value;
    /**
     * if is left value
     */
    private boolean isLeft;

    /**
     * @param value the value
     * @param isLeft if is left value
     */
    public Either(Object value, boolean isLeft) {
        this.value = value;
        this.isLeft = isLeft;
    }

    /**
     * @param left the left value
     * @param <L> the left type
     * @param <R> the right type
     * @return the value of the Either type
     */
    public static <L,R>  Either<L,R> left(L left) {
        return new Either<>(left, true);
    }

    /**
     * @param right the right value
     * @param <L> the left type
     * @param <R> the right type
     * @return the value of the Either type
     */
    public static <L,R>  Either<L,R> right(R right) {
        return new Either<>(right, false);
    }


    /**
     * @param either the Either
     * @param <L> the left type
     * @param <R> the right type
     * @return the left value
     */
    public static <L,R> L getLeft(Either<L,R> either) {
        if (!either.isLeft) {
            throw new RuntimeException("This either instance does not have left value");
        }
        return (L) either.value;
    }

    /**
     * @param either the Either
     * @param <L> the left type
     * @param <R> the right type
     * @return the right value
     */
    public static <L,R> R getRight(Either<L,R> either) {
        if (either.isLeft) {
            throw new RuntimeException("This either instance does not have left value");
        }
        return (R) either.value;
    }

    /**
     * @param either the Either value
     * @param <L> the left type
     * @param <R> the right type
     * @return is if left
     */
    public static <L,R> boolean isLeft(Either<L,R> either) {
        return either.isLeft;
    }

    /**
     * @param either the Either value
     * @param <L> the left type
     * @param <R> the right type
     * @return is if right
     */
    public static <L,R> boolean isRight(Either<L,R> either) {
        return !either.isLeft;
    }
}
