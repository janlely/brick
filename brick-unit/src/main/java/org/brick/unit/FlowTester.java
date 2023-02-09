package org.brick.unit;


import org.brick.Flow;
import org.brick.FlowDoc;
import org.brick.UnitFunction;
import org.brick.types.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FlowTester<I,O,C> {

    public boolean run(I input, C context) {
        throw new RuntimeException("Method not implemented");
    }


    public <T> Builder<I,O,C,T> startUnit(UnitFunction<I,T,C> unit) {
        return new Builder<I,O,C,I>().linkUnit(unit);
    }

    public <T> Builder<I,O,C,T> startFlow(Flow<I,T,C> flow) {
        return new Builder<I,O,C,I>().linkFlow(flow);
    }

    public static class Builder<I,O,C,T> {
        Function<O,Boolean> passCond;
//        Either<UnitFunction<I,O,C>, Flow<I,O,C>> target;

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
                    return passCond.apply((O) i);
                }
            };
        }

        public <O1> Builder<I,O,C,O1> linkUnit(UnitFunction<T,O1,C> unit) {
            this.units.add(Either.right(unit));
            return (Builder<I, O, C, O1>) this;
        }

        public <O1> Builder<I,O,C,O1> linkFlow(Flow<T,O1,C> flow) {
            this.units.add(Either.left(flow));
            return (Builder<I, O, C, O1>) this;
        }

        public Builder<I,O,C,T> pass(Function<O,Boolean> passCond) {
            this.passCond = passCond;
            return this;
        }



    }
}
