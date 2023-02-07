package org.brick.unit;


import org.brick.Flow;
import org.brick.UnitFunction;
import org.brick.types.Either;

import java.util.function.Function;

public interface FlowTester {

    boolean run();

    class Builder<I,O,C> {
        I input;
        C context;
        Function<O,Boolean> passCond;
        Either<UnitFunction<I,O,C>, Flow<I,O,C>> target;

        FlowTester build() {
            return () -> passCond.apply(Either.isLeft(target)
                    ? Either.getLeft(target).exec(input, context)
                    : Either.getRight(target).run(input, context));
        }

        public <U extends UnitFunction<I,O,C>> Builder<I,O,C> targetUnit(U unit) {
            this.target = Either.left(unit);
            return this;
        }

        public <F extends Flow<I,O,C>> Builder<I,O,C> targetUnit(F flow) {
            this.target = Either.right(flow);
            return this;
        }
        public Builder<I,O,C> input(I input) {
            this.input = input;
            return this;
        }
        public Builder<I,O,C> context(C context) {
            this.context = context;
            return this;
        }
        public Builder<I,O,C> pass(Function<O,Boolean> passCond) {
            this.passCond = passCond;
            return this;
        }



    }
}
