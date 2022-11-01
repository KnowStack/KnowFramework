package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.root;

import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.root.Size;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.value.ObjectNode;

public class SizeParser extends DslParser {
    public SizeParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) {
        Size node = new Size(name);
        node.v = new ObjectNode(obj);
        return node;
    }
}
