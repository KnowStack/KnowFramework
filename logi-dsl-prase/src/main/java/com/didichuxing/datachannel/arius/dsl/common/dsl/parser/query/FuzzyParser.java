package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.Exists;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.Fuzzy;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;
import com.didichuxing.datachannel.arius.dsl.common.dsl.util.ConstValue;

public class FuzzyParser extends DslParser {

    public FuzzyParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Fuzzy node = new Fuzzy(name);
        NodeMap nm = new NodeMap();

        NodeMap.toField4Value((JSONObject) obj, nm);

        node.n = nm;
        return node;
    }
}
