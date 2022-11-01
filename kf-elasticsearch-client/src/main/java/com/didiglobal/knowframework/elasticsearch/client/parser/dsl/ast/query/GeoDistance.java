package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.query;

import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.Node;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.visitor.basic.Visitor;

public class GeoDistance extends KeyWord {
    public Node n;

    public GeoDistance(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
