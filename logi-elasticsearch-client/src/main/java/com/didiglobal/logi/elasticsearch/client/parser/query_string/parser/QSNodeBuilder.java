package com.didiglobal.logi.elasticsearch.client.parser.query_string.parser;


import com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.QSFieldNode;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.QSNode;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.QSValueNode;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.op.*;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.op.common.QSBinaryOpNode;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.op.common.QSSingleOpNode;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.op.logic.QSANDNode;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.op.logic.QSORNode;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.op.logic.QSParenNode;

import java.util.Stack;

public class QSNodeBuilder {

    private Stack<QSNode> stack = new Stack<>();


    public QSNode toNode() throws ParseException {
        if (stack.size() != 1) {
            throw new ParseException("parse error");
        }

        return stack.pop();
    }


    public void addParen(QSNode node, Token boost) throws ParseException {
        QSParenNode pn = new QSParenNode("");
        pn.setNode(node);
        pn.setBoost(boost);

        putStack(pn);
    }


    public void addFieldEq(Token src) throws ParseException {
        QSFieldNode qfn = new QSFieldNode(getImage(src));
        QSEQNode qen = new QSEQNode();
        qen.setLeft(qfn);

        putStack(qen);
    }


    public void addAnd(Token src) throws ParseException {
        QSANDNode an = new QSANDNode(getImage(src));

        putStack(an);
    }


    public void addOr(Token src) throws ParseException {
        QSORNode rn = new QSORNode(getImage(src));

        putStack(rn);
    }


    public void addMinus(Token src) throws ParseException {
        QSMinusNode mn = new QSMinusNode(getImage(src));

        putStack(mn);
    }


    public void addNot(Token src) throws ParseException {
        QSNotNode nn = new QSNotNode(getImage(src));

        putStack(nn);
    }


    public void addPlus(Token src) throws ParseException {
        QSPlusNode pn = new QSPlusNode(getImage(src));

        putStack(pn);
    }


    public void addValue(Token src, Token fuzzySlop, Token boost) throws ParseException {
        QSValueNode vn = new QSValueNode(getImage(src), getImage(fuzzySlop), getImage(boost));

        putStack(vn);
    }


    public void addRange(Token start, Token end, boolean startInc, boolean endInc) throws ParseException {
        QSValueNode lvn = new QSValueNode(getImage(start), null, null);
        QSValueNode rvn = new QSValueNode(getImage(end), null, null);

        QSRangeNode rangeNode = new QSRangeNode(startInc, endInc);
        rangeNode.setLeft(lvn);
        rangeNode.setRight(rvn);

        putStack(rangeNode);
    }


    private String getImage(Token t) throws ParseException {
        if (t == null) {
            return null;
        }
        return StringUtils.discardEscapeChar(t.image);
    }


    private void putStack(QSNode node) throws ParseException {


        if (stack.size() == 0) {
            stack.push(node);
            return;
        }


        if (!node.completeParse()) {
            stack.push(node);
            return;
        }


        QSNode sn = stack.peek();
        if (sn.completeParse()) {
            stack.pop();


            QSORNode or = new QSORNode("OR");
            or.setLeft(sn);
            or.setRight(node);

            putStack(or);
            return;
        }


        if (sn instanceof QSSingleOpNode) {
            stack.pop();
            ((QSSingleOpNode) sn).setNode(node);

            putStack(sn);
            return;
        }


        if (sn instanceof QSBinaryOpNode) {
            stack.pop();
            QSBinaryOpNode bop = (QSBinaryOpNode) sn;


            if (bop.getNeedValue() == 2) {
                if (stack.size() == 0) {
                    throw new ParseException("parse error, size==0 node:" + node.getSource());
                }
                QSNode left = stack.pop();

                ((QSBinaryOpNode) sn).setLeft(left);
            }

            bop.setRight(node);

            putStack(bop);
            return;
        }

        throw new ParseException("parse error, node:" + node.getSource());
    }

}
