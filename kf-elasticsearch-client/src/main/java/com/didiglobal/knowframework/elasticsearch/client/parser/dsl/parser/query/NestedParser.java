package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.Node;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.query.Nested;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserRegister;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.key.StringNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.value.ValueNode;

public class NestedParser extends DslParser {
    public NestedParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object root) throws Exception {
        Nested node = new Nested(name);
        NodeMap nm = new NodeMap();


        JSONObject obj = (JSONObject) root;
        for (String key : obj.keySet()) {
            Node valueNode;

            if (key.equalsIgnoreCase("query")) {
                valueNode = ParserRegister.parse(parserType, key, obj.get(key));
            } else {
                valueNode = ValueNode.getValueNode(obj.get(key));
            }

            nm.m.put(new StringNode(key), valueNode);
        }


        node.n = nm;
        return node;
    }
}
