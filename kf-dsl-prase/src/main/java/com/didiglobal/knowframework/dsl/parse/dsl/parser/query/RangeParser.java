package com.didiglobal.knowframework.dsl.parse.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.query.Range;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.DslParser;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.ParserType;

public class RangeParser extends DslParser {
    public RangeParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Range node = new Range(name);
        NodeMap.toField4Value((JSONObject) obj, node.m);
        return node;
    }
}
