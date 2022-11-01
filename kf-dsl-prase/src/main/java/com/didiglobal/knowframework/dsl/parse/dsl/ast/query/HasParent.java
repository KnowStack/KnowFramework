package com.didiglobal.knowframework.dsl.parse.dsl.ast.query;

import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;

public class HasParent extends KeyWord {
    public static final String QUERY_STR = "query";

    public NodeMap m = new NodeMap();


    public HasParent(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
