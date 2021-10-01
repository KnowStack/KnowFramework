package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.root;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.Node;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.KeyNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ObjectNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ValueNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.Body;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.From;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserRegister;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

public class BodyParser extends DslParser {

    public BodyParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Body node = new Body(name);
        NodeMap nm = new NodeMap();
        JSONObject jsonObject = (JSONObject) obj;

         for (String key : jsonObject.keySet()) {
             KeyNode keyNode = new StringNode(key);

             Node valueNode = ParserRegister.parse(parserType, key, jsonObject.get(key));
             if (valueNode == null) {
                 valueNode = ValueNode.getValueNode(jsonObject.get(key));
             }

             nm.m.put(keyNode, valueNode);
         }

        node.n = nm;
        return node;
    }
}
