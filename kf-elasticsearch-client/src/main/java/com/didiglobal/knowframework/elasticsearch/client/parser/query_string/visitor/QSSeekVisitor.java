package com.didiglobal.knowframework.elasticsearch.client.parser.query_string.visitor;


import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.QSFieldNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.QSValueNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.*;
import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.common.QSBinaryOpNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.common.QSSingleOpNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.logic.QSANDNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.logic.QSORNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.query_string.ast.op.logic.QSParenNode;


public class QSSeekVisitor implements QSVisitor {
    @Override
    public void visit(QSValueNode node) {
    }

    @Override
    public void visit(QSFieldNode node) {
    }

    @Override
    public void visit(QSParenNode node) {
        node.getNode().accept(this);
    }

    @Override
    public void visit(QSANDNode node) {
        doBinary(node);
    }

    @Override
    public void visit(QSORNode node) {
        doBinary(node);
    }

    @Override
    public void visit(QSEQNode node) {
        doBinary(node);
    }

    @Override
    public void visit(QSMinusNode node) {
        doSingle(node);
    }

    @Override
    public void visit(QSNotNode node) {
        doSingle(node);
    }

    @Override
    public void visit(QSPlusNode node) {
        doSingle(node);
    }

    @Override
    public void visit(QSRangeNode node) {
        doBinary(node);
    }


    private void doBinary(QSBinaryOpNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
    }

    private void doSingle(QSSingleOpNode node) {
        node.getNode().accept(this);
    }
}
