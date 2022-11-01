package com.didiglobal.knowframework.dsl.parse.dsl.ast.query;

import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.Node;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;

public class MoreLikeThis extends KeyWord {
    public Node n;

    public MoreLikeThis(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
