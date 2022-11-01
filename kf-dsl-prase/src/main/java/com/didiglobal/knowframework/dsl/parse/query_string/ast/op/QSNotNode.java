package com.didiglobal.knowframework.dsl.parse.query_string.ast.op;

import com.didiglobal.knowframework.dsl.parse.query_string.ast.op.common.QSSingleOpNode;
import com.didiglobal.knowframework.dsl.parse.query_string.visitor.QSVisitor;

public class QSNotNode extends QSSingleOpNode {

    public QSNotNode(String source) {
        super(source);
    }

    @Override
    public void accept(QSVisitor vistor) {
        vistor.visit(this);
    }
}
