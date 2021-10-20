package com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor;

import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.key.FieldNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.QueryStringValueNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor.basic.SeekVisitor;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.visitor.QSExportFieldVisitor;

import java.util.HashSet;
import java.util.Set;

public class GetInfoVisitor extends SeekVisitor {
    public Set<String> fields = new HashSet<>();

    public boolean isQueryString = false;

    public boolean isScript = false;

    @Override
    public void visit(FieldNode node) {
        fields.add(formatField(node.value));
    }

    @Override
    public void visit(QueryStringValueNode node) {
        QSExportFieldVisitor qsExportFieldVisitor = new QSExportFieldVisitor();
        node.getQsNode().accept(qsExportFieldVisitor);
        fields.addAll(qsExportFieldVisitor.getFieldNameSet());
    }


    private String formatField(String field) {
        if (field == null) {
            return field;
        }

        field = field.trim();

        // 去除的 \ 和 "
        field = field.replaceAll("\\\\", "");
        field = field.replaceAll("\"", "");

        return field;
    }
}
