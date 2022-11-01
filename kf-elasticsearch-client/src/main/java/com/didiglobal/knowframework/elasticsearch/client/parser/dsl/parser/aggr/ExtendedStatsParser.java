package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.aggr;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.aggr.ExtendedStats;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserRegister;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.Node;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.key.FieldNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.key.StringNode;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.value.ValueNode;

public class ExtendedStatsParser extends DslParser {

    public ExtendedStatsParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        ExtendedStats node = new ExtendedStats(name);
        NodeMap nm = new NodeMap();

        JSONObject jsonObject = (JSONObject) obj;
        for (String key : jsonObject.keySet()) {
            Node value;

            if (key.equalsIgnoreCase("field")) {
                value = new FieldNode(jsonObject.get(key));
            } else if (key.equalsIgnoreCase("script")) {
                value = ParserRegister.parse(parserType, key, jsonObject.get(key));
            } else {
                value = ValueNode.getValueNode(jsonObject.get(key));
            }

            nm.m.put(new StringNode(key), value);
        }

        node.n = nm;
        return node;
    }
}
