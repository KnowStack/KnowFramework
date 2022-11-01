package com.didiglobal.knowframework.dsl.parse.dsl.parser.root;

import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.root.Size;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.value.ObjectNode;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.DslParser;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.ParserType;

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
