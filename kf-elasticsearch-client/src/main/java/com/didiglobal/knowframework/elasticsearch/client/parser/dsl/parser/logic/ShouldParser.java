package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.logic;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.logic.Should;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeList;

public class ShouldParser extends DslParser {

    public ShouldParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Should node = new Should(name);
        node.n = NodeList.toNodeList(parserType, (JSON) obj, false);
        return node;
    }
}
