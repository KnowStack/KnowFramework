package com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.op.common;

import com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.QSNode;

public abstract class QSOPNode extends QSNode {

    public QSOPNode(String source, int needValue) {
        super(source, needValue);
    }
}
