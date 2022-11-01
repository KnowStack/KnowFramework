package com.didiglobal.knowframework.dsl.parse.dsl.ast.common.key;

import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;

public class FieldNode extends KeyNode {

    public FieldNode(Object obj) {
        super(obj);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
