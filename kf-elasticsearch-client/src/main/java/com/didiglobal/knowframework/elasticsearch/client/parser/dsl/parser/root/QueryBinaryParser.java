package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.root;

import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.root.QueryBinary;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.value.ObjectNode;

public class QueryBinaryParser extends DslParser {
    public QueryBinaryParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) {
        QueryBinary node = new QueryBinary(name);
        node.n = new ObjectNode(obj);
        return node;
    }
}
