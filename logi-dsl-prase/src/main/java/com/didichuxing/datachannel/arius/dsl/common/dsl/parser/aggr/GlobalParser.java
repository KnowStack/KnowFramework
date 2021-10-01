package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.aggr;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.aggr.Global;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ValueNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

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
