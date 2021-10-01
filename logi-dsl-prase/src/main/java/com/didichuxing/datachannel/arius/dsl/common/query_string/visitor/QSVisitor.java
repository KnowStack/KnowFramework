package com.didichuxing.datachannel.arius.dsl.common.query_string.visitor;


import com.didichuxing.datachannel.arius.dsl.common.query_string.ast.QSFieldNode;
import com.didichuxing.datachannel.arius.dsl.common.query_string.ast.QSValueNode;
import com.didichuxing.datachannel.arius.dsl.common.query_string.ast.op.*;
import com.didichuxing.datachannel.arius.dsl.common.query_string.ast.op.logic.QSANDNode;
import com.didichuxing.datachannel.arius.dsl.common.query_string.ast.op.logic.QSORNode;
import com.didichuxing.datachannel.arius.dsl.common.query_string.ast.op.logic.QSParenNode;


public interface QSVisitor {
    public void visit(QSValueNode node);

    public void visit(QSFieldNode node);

    public void visit(QSParenNode node);

    public void visit(QSANDNode node);

    public void visit(QSORNode node);

    public void visit(QSEQNode node);

    public void visit(QSMinusNode node);

    public void visit(QSNotNode node);

    public void visit(QSPlusNode node);

    public void visit(QSRangeNode node);
}
