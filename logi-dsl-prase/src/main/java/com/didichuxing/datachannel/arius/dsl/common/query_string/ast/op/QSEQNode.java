package com.didichuxing.datachannel.arius.dsl.common.query_string.ast.op;

import com.didichuxing.datachannel.arius.dsl.common.query_string.ast.op.common.QSBinaryOpNode;
import com.didichuxing.datachannel.arius.dsl.common.query_string.visitor.QSVisitor;

public class QSEQNode extends QSBinaryOpNode {
    public QSEQNode() {
        super(":");
    }

    @Override
    public void accept(QSVisitor vistor) {
        vistor.visit(this);
    }
}
