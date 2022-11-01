package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.root;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.Node;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.root.Body;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserRegister;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.key.KeyNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.key.StringNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.value.ValueNode;

public class BodyParser extends DslParser {

    public BodyParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Body node = new Body(name);
        NodeMap nm = new NodeMap();
        JSONObject jsonObject = (JSONObject) obj;

        for (String key : jsonObject.keySet()) {
            KeyNode keyNode = new StringNode(key);

            Node valueNode = ParserRegister.parse(parserType, key, jsonObject.get(key));
            if (valueNode == null) {
                valueNode = ValueNode.getValueNode(jsonObject.get(key));
            }

            nm.m.put(keyNode, valueNode);
        }

        node.n = nm;
        return node;
    }
}
