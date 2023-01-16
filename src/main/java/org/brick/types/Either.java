package org.brick.types;

/**
 * like Either in Haskell
 * @param <L>
 * @param <R>
 */
public class Either<L,R> {

    private Object value;
    private boolean isLeft;

    public Either(Object value, boolean isLeft) {
        this.value = value;
        this.isLeft = isLeft;
    }

    public static <L,R>  Either<L,R> left(L left) {
        return new Either<>(left, true);
    }

    public static <L,R>  Either<L,R> right(R right) {
        return new Either<>(right, false);
    }


    public static <L,R> L getLeft(Either<L,R> either) {
        if (!either.isLeft) {
            throw new RuntimeException("This either instance does not have left value");
        }
        return (L) either.value;
    }

    public static <L,R> R getRight(Either<L,R> either) {
        if (either.isLeft) {
            throw new RuntimeException("This either instance does not have left value");
        }
        return (R) either.value;
    }

    public static <L,R> boolean isLeft(Either<L,R> either) {
        return either.isLeft;
    }

    public static <L,R> boolean isRight(Either<L,R> either) {
        return !either.isLeft;
    }
}
