package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.query;

import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.visitor.basic.Visitor;

public class Range extends KeyWord {

    public NodeMap m = new NodeMap();

    public Range(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
