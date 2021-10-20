package com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.query;

import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor.basic.Visitor;

public class Negative extends KeyWord {

    public NodeMap m = new NodeMap();

    public Negative(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
