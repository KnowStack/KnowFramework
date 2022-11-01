package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.root;

import com.alibaba.fastjson.JSONArray;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.root.FieldDataFields;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.key.StringNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeList;

/**
 * 解析fielddata_fields关键字
 * "fielddata_fields": [
 * "sinkTime",
 * "collectTime",
 * "logTime",
 * "cleanTime"
 * ]
 */
public class FieldDataFieldsParser extends DslParser {

    public FieldDataFieldsParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object root) throws Exception {
        FieldDataFields node = new FieldDataFields(name);

        if (root instanceof JSONArray) {
            node.n = new NodeList();
            NodeList.toFieldList((JSONArray) root, (NodeList) node.n);

        } else if (root instanceof String) {
            node.n = new StringNode(root);
        }

        return node;
    }

}
