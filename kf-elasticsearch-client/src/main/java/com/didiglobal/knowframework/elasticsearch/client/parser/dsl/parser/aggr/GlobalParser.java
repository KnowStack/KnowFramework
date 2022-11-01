package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.aggr;

import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.aggr.Global;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.value.ValueNode;

public class GlobalParser extends DslParser {

    public GlobalParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Global node = new Global(name);
        node.n = ValueNode.getValueNode(obj);
        return node;
    }
}
