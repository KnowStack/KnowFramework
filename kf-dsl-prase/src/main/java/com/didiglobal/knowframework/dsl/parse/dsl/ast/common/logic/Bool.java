package com.didiglobal.knowframework.dsl.parse.dsl.ast.common.logic;

import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.multi.NodeMap;

public class Bool extends KeyWord {
    public NodeMap m = new NodeMap();

    public Bool(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
