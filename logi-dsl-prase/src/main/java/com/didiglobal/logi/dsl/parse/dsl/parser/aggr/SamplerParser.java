package com.didiglobal.logi.dsl.parse.dsl.parser.aggr;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.logi.dsl.parse.dsl.ast.aggr.Sampler;
import com.didiglobal.logi.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.logi.dsl.parse.dsl.ast.common.multi.NodeMap;
import com.didiglobal.logi.dsl.parse.dsl.parser.DslParser;
import com.didiglobal.logi.dsl.parse.dsl.parser.ParserType;

public class SamplerParser extends DslParser {

    public SamplerParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Sampler node = new Sampler(name);

        NodeMap nm = new NodeMap();
        NodeMap.toString2ValueWithField(parserType, (JSONObject) obj, nm, null);
        node.n = nm;

        return node;
    }
}
