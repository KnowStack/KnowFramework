package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.Node;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ValueNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.Nested;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.Terms;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserRegister;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

public class NestedParser extends DslParser {
    public NestedParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object root) throws Exception {
        Nested node = new Nested(name);
        NodeMap nm = new NodeMap();


        JSONObject obj = (JSONObject) root;
        for(String key : obj.keySet()) {
            Node valueNode;

            if(key.equalsIgnoreCase("query")) {
                valueNode = ParserRegister.parse(parserType, key, obj.get(key));
            } else {
                valueNode = ValueNode.getValueNode(obj.get(key));
            }

            nm.m.put(new StringNode(key), valueNode);
        }


        node.n = nm;
        return node;
    }
}
