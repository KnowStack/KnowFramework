package com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value;

import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.Visitor;
import com.didichuxing.datachannel.arius.dsl.common.query_string.ast.QSNode;

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
