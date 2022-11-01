package com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.logic;

import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.visitor.QSVisitor;
import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.common.QSBinaryOpNode;

public class QSORNode extends QSBinaryOpNode {

    public QSORNode(String source) {
        super(source);
    }

    @Override
    public void accept(QSVisitor vistor) {
        vistor.visit(this);
    }
}
