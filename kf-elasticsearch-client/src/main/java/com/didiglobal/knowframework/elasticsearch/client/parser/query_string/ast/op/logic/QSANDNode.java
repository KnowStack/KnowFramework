package com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.logic;


import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.visitor.QSVisitor;
import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.common.QSBinaryOpNode;

public class QSANDNode extends QSBinaryOpNode {
    public QSANDNode(String source) {
        super(source);
    }

    @Override
    public void accept(QSVisitor vistor) {
        vistor.visit(this);
    }
}
