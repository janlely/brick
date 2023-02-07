package org.brick.unit;

import org.brick.Flow;
import org.brick.FlowDoc;
import org.brick.PureFunction;
import org.brick.UnitFunction;
import org.brick.types.Either;

import java.util.ArrayList;
import java.util.List;

public class FlowLinker<I,O,C> {

    public <T, U extends UnitFunction<I,T,C>> Builder<I,O,C,T> startUnit(U unit) {
        return new Builder<I,O,C,I>().linkUnit(unit);
    }

    public <T, F extends Flow<I,T,C>> Builder<I,O,C,T> startFlow(F flow) {
        return new Builder<I,O,C,I>().linkFlow(flow);
    }

    public static class Builder<I,O,C,T> {

        List<Either<Flow, UnitFunction>> units;

        public Builder() {
            this.units = new ArrayList<>();
        }


        public <O1, U extends UnitFunction<T,O1,C>> Builder<I,O,C,O1> linkUnit(U unit) {
            this.units.add(Either.right(unit));
            return (Builder<I, O, C, O1>) this;
        }

        public <O1, F extends Flow<T,O1,C>> Builder<I,O,C,O1> linkFlow(F flow) {
            this.units.add(Either.left(flow));
            return (Builder<I, O, C, O1>) this;
        }

        public Flow<I,T,C> end() {
            return new Flow<>() {
                @Override
                public FlowDoc<I, T, C> getFlowDoc() {
                    return null;
                }

                @Override
                public String getFlowName() {
                    return null;
                }

                @Override
                public T run(I input, C context) {
                    Object i = input;
                    for (Either<Flow, UnitFunction> unit : units) {
                        i = Either.isLeft(unit)
                                ? Either.getLeft(unit).run(i, context)
                                : Either.getRight(unit).exec(i, context);
                    }
                    return (T) i;
                }
            };
        }
    }
}
