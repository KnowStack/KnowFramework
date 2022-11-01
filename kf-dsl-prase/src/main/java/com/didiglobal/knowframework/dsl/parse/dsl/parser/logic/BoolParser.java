package com.didiglobal.knowframework.dsl.parse.dsl.parser.logic;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.logic.Bool;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.DslParser;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.ParserType;

public class BoolParser extends DslParser {
    public BoolParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Bool node = new Bool(name);
        NodeMap.toString2Node(parserType, (JSONObject)obj, node.m);
        return node;
    }
}
