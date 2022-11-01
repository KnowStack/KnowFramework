package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.query.ConstantScore;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserRegister;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.key.StringNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.value.ValueNode;

public class ConstantScoreParser extends DslParser {
    public ConstantScoreParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        ConstantScore node = new ConstantScore(name);

        NodeMap nm = new NodeMap();
        JSONObject jsonObj = (JSONObject) obj;
        for (String key : jsonObj.keySet()) {
            if (key.equalsIgnoreCase("filter")) {
                nm.m.put(new StringNode(key), ParserRegister.parse(parserType, key, jsonObj.get(key)));
            } else {
                nm.m.put(new StringNode(key), ValueNode.getValueNode(jsonObj.get(key)));
            }
        }
        node.n = nm;

        return node;
    }
}
