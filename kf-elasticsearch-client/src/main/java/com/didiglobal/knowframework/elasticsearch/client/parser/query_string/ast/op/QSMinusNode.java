package com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op;

import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.visitor.QSVisitor;
import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.common.QSSingleOpNode;

public class QSMinusNode extends QSSingleOpNode {
    public QSMinusNode(String source) {
        super(source);
    }

    @Override
    public void accept(QSVisitor vistor) {
        vistor.visit(this);
    }
}
