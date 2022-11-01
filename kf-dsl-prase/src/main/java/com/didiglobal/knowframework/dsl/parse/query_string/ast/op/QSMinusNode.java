package com.didiglobal.knowframework.dsl.parse.query_string.ast.op;

import com.didiglobal.knowframework.dsl.parse.query_string.ast.op.common.QSSingleOpNode;
import com.didiglobal.knowframework.dsl.parse.query_string.visitor.QSVisitor;

public class QSMinusNode extends QSSingleOpNode {
    public QSMinusNode(String source) {
        super(source);
    }

    @Override
    public void accept(QSVisitor vistor) {
        vistor.visit(this);
    }
}
