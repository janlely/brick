package org.brick.types;

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

    public L left() {
        if (!this.isLeft) {
            throw new RuntimeException("This either instance does not have left value");
        }
        return (L) this.value;
    }

    public R right() {
        if (this.isLeft) {
            throw new RuntimeException("This either instance does not have left value");
        }
        return (R) this.value;
    }

    public boolean isLeft() {
        return this.isLeft;
    }

    public boolean isRight() {
        return !this.isLeft;
    }
}
