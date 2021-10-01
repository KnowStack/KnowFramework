package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.Match;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.MultiMatch;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

public class MultiMatchParser extends DslParser {

    public MultiMatchParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        MultiMatch node = new MultiMatch(name);
        NodeMap nm = new NodeMap();

        NodeMap.toString2ValueWithField(parserType, (JSONObject) obj, nm,"fields");

        node.n = nm;
        return node;
    }
}
