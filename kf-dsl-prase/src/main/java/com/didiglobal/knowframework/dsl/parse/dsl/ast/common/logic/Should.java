package com.didiglobal.knowframework.dsl.parse.dsl.ast.common.logic;

import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.Node;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;

public class Should extends KeyWord {

    public Node n;

    public Should(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
