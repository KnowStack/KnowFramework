package com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op;

import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.visitor.QSVisitor;
import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.common.QSBinaryOpNode;

public class QSEQNode extends QSBinaryOpNode {
    public QSEQNode() {
        super(":");
    }

    @Override
    public void accept(QSVisitor vistor) {
        vistor.visit(this);
    }
}
