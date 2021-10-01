package com.didichuxing.datachannel.arius.dsl.common.query_string.ast.op.logic;

import com.didichuxing.datachannel.arius.dsl.common.query_string.ast.op.common.QSBinaryOpNode;
import com.didichuxing.datachannel.arius.dsl.common.query_string.visitor.QSVisitor;

public class QSORNode extends QSBinaryOpNode {

    public QSORNode(String source) {
        super(source);
    }

    @Override
    public void accept(QSVisitor vistor) {
        vistor.visit(this);
    }
}
