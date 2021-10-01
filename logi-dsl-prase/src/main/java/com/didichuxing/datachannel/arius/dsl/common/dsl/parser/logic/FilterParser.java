package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.logic;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeList;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.logic.Filter;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

public class FilterParser extends DslParser {

    public FilterParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Filter node = new Filter(name);
        node.n = NodeList.toNodeList(parserType, (JSON) obj, true);
        return node;
    }
}
