package com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.root;

import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.Node;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor.basic.Visitor;

public class IgnoreUnavailable extends KeyWord {
    public Node n;

    public IgnoreUnavailable(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
