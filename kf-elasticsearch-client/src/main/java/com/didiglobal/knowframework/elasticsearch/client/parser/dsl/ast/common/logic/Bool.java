package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.logic;

import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.visitor.basic.Visitor;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;

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
