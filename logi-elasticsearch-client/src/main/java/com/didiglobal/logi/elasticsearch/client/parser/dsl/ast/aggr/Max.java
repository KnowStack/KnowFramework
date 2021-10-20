package com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.aggr;

import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.Node;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor.basic.Visitor;


public class Max extends KeyWord {

    public Node n;

    public Max(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
