package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.Negative;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserRegister;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

/**
 * @author D10865
 *
 * 解析boosting子查询中negative子句
 */
public class NegativeParser extends DslParser {

    public NegativeParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Negative node = new Negative(name);

        JSONObject jsonObj = (JSONObject) obj;
        for(String key : jsonObj.keySet()) {
            node.m.m.put(new StringNode(key), ParserRegister.parse(parserType, key, jsonObj.get(key)));
        }

        return node;
    }

}
