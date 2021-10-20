package com.didiglobal.logi.elasticsearch.client.parser.dsl.parser.query;

import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.ValueNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.query.MatchNone;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.parser.ParserType;

public class MatchNoneParser extends DslParser {
    public MatchNoneParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object root) throws Exception {
        MatchNone node = new MatchNone(name);
        node.n = ValueNode.getValueNode(root);
        return node;
    }
}
