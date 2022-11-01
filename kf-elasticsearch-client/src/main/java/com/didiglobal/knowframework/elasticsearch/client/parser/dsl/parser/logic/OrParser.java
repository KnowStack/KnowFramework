package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.logic;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.logic.Or;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeList;

public class OrParser extends DslParser {

    public OrParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Or node = new Or(name);
        node.n = NodeList.toNodeList(parserType, (JSON) obj, false);
        return node;
    }
}
