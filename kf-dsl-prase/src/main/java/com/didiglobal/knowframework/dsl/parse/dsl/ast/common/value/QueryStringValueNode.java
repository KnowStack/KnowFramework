package com.didiglobal.knowframework.dsl.parse.dsl.ast.common.value;

import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;
import com.didiglobal.knowframework.dsl.parse.query_string.ast.QSNode;

public class QueryStringValueNode extends ValueNode {
    private QSNode qsNode;

    public QSNode getQsNode() {
        return qsNode;
    }

    public void setQsNode(QSNode qsNode) {
        this.qsNode = qsNode;
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
