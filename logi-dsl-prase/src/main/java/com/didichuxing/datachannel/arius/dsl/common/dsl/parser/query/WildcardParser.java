package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.Wildcard;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

public class WildcardParser extends DslParser {
    public WildcardParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Wildcard node = new Wildcard(name);
        NodeMap.toField4Value((JSONObject) obj, node.m);
        return node;
    }
}
