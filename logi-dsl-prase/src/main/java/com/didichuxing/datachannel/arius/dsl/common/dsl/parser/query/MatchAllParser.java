package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.query;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ValueNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.MatchAll;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

public class MatchAllParser extends DslParser {
    public MatchAllParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object root) throws Exception {
        MatchAll node = new MatchAll(name);
        node.n = ValueNode.getValueNode(root);
        return node;
    }
}
