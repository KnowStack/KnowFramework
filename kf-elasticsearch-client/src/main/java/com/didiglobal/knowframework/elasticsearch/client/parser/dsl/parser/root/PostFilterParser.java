package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.root;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.root.PostFilter;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeList;

public class PostFilterParser extends DslParser {
    public PostFilterParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        PostFilter node = new PostFilter(name);

        node.n = NodeList.toNodeList(parserType, (JSON) obj, true);

        return node;
    }
}
