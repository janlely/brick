package org.brick.unit;


import org.brick.Flow;
import org.brick.UnitFunction;
import org.brick.types.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FlowTester<I,O,C> {

    public boolean run(I input, C context) {
        throw new RuntimeException("Method not implemented");
    }

    public <T> Builder<I,C,T> linkUnit(UnitFunction<I,T,C> unit) {
        return new Builder<I,C,I>().linkUnit(unit);
    }

    public <T> Builder<I,C,T> linkFlow(Flow<I,T,C> flow) {
        return new Builder<I,C,I>().linkFlow(flow);
    }

    public static class Builder<I,C,T> {
        Function<T,Boolean> passCond;

        List<Either<Flow, UnitFunction>> units;

        public Builder() {
            this.units = new ArrayList<>();
        }

        public FlowTester<I,T,C> build() {
            return new FlowTester<>() {
                @Override
                public boolean run(I input, C context) {
                    Object i = input;
                    for (Either<Flow, UnitFunction> unit : units) {
                        i = Either.isLeft(unit)
                                ? Either.getLeft(unit).run(i, context)
                                : Either.getRight(unit).exec(i, context);
                    }
                    return passCond.apply((T) i);
                }
            };
        }

        public <O1> Builder<I,C,O1> linkUnit(UnitFunction<T,O1,C> unit) {
            this.units.add(Either.right(unit));
            return (Builder<I, C, O1>) this;
        }

        public <O1> Builder<I,C,O1> linkFlow(Flow<T,O1,C> flow) {
            this.units.add(Either.left(flow));
            return (Builder<I, C, O1>) this;
        }

        public Builder<I,C,T> pass(Function<T,Boolean> passCond) {
            this.passCond = passCond;
            return this;
        }

    }
}
