package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.MatchPhrasePrefix;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.Term;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

public class MatchPhrasePrefixParser extends DslParser {
    public MatchPhrasePrefixParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        MatchPhrasePrefix node = new MatchPhrasePrefix(name);
        NodeMap nm = new NodeMap();

        NodeMap.toField4Value((JSONObject) obj, nm);

        node.n = nm;
        return node;
    }
}
