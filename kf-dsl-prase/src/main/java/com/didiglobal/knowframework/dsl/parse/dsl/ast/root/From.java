package com.didiglobal.knowframework.dsl.parse.dsl.ast.root;

import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.value.ValueNode;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;

public class From extends KeyWord {
    public ValueNode v;

    public From(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
