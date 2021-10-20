package com.didiglobal.logi.elasticsearch.client.parser.dsl.parser.root;

import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.ObjectNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.root.Index;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.parser.ParserType;


public class IndexParser extends DslParser {

    public IndexParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Index node = new Index(name);
        node.n = new ObjectNode(obj);

        return node;
    }

}
