package com.didiglobal.logi.elasticsearch.client.parser.dsl.parser.root;

import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.ObjectNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.root.FieldFormatMap;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.parser.ParserType;

public class FieldFormatMapParser extends DslParser {

    public FieldFormatMapParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        FieldFormatMap node = new FieldFormatMap(name);
        node.n = new ObjectNode(obj);

        return node;
    }

}
