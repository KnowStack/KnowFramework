package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.aggr;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.aggr.ValueCount;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.util.ConstValue;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;

public class ValueCountParser extends DslParser {

    public ValueCountParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        ValueCount node = new ValueCount(name);

        NodeMap nm = new NodeMap();
        NodeMap.toString2ValueWithField(parserType, (JSONObject) obj, nm, ConstValue.FIELD);
        node.n = nm;

        return node;
    }
}
