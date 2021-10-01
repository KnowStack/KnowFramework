package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.Positive;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserRegister;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

/**
 * @author D10865
 *
 * 解析boosting子查询中positive子句
 */
public class PositiveParser extends DslParser {

    public PositiveParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Positive node = new Positive(name);

        JSONObject jsonObj = (JSONObject) obj;
        for(String key : jsonObj.keySet()) {
            node.m.m.put(new StringNode(key), ParserRegister.parse(parserType, key, jsonObj.get(key)));
        }

        return node;
    }

}
