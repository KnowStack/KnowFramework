package com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.root;

import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.Node;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor.basic.Visitor;

/**
 * @author D10865
 */
public class Fields extends KeyWord {
    public Node n;

    public Fields(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
