package com.didiglobal.knowframework.dsl.parse.dsl.ast.root;

import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.Node;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;

public class Rescore extends KeyWord {
    public Node n;

    public Rescore(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
