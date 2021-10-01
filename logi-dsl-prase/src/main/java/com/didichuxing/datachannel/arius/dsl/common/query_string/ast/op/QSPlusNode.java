package com.didichuxing.datachannel.arius.dsl.common.query_string.ast.op;

import com.didichuxing.datachannel.arius.dsl.common.query_string.ast.op.common.QSSingleOpNode;
import com.didichuxing.datachannel.arius.dsl.common.query_string.visitor.QSVisitor;

public class QSPlusNode extends QSSingleOpNode {
    public QSPlusNode(String source) {
        super(source);
    }

    @Override
    public void accept(QSVisitor vistor) {
        vistor.visit(this);
    }
}
