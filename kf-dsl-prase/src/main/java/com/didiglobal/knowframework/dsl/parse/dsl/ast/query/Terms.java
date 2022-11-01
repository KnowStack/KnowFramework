package com.didiglobal.knowframework.dsl.parse.dsl.ast.query;

import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;

public class Terms extends KeyWord {
    public NodeMap m = new NodeMap();

    public Terms(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
