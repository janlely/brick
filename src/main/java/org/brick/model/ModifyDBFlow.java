package org.brick.model;

import net.jodah.typetools.TypeResolver;
import org.brick.core.FlowDoc;

import java.util.function.BiFunction;

public class ModifyDBFlow<I,O,C> implements IModifyDBFlow<I,O,C>{

    private String desc;
    private String sql;

    private BiFunction<I,C,O> func;


    public ModifyDBFlow(String desc, String sql, BiFunction<I,C,O> func)  {
        this.desc = desc;
        this.sql = sql;
        this.func = func;
    }
    @Override
    public FlowDoc<I, O, C> getFlowDoc() {
        FlowDoc<I,O,C> flowDoc = new FlowDoc<>(this.desc, getFlowType());
        Class<?>[] classes = TypeResolver.resolveRawArguments(ModifyDBFlow.class, this.getClass());
        return flowDoc.types((Class<I>) classes[0], (Class<O>) classes[1], (Class<C>) classes[2]);
    }

    @Override
    public String getPseudoSql() {
        return this.sql;
    }

    @Override
    public O doDBModify(I input, C context) {
        return this.func.apply(input, context);
    }
}
