package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.root;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.Node;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.IdentityNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.KeyNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ValueNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.Body;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.ScriptFields;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserRegister;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

public class ScriptFieldsParser extends DslParser {

    public ScriptFieldsParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        ScriptFields node = new ScriptFields(name);
        NodeMap nm = new NodeMap();

        JSONObject jsonObject = (JSONObject) obj;
        for(String key : jsonObject.keySet()) {
            nm.m.put(new IdentityNode(key), parserOne((JSONObject) jsonObject.get(key)));
        }

        node.n = nm;
        return node;
    }

    private Node parserOne(JSONObject root) throws Exception {
        NodeMap nm = new NodeMap();

        for(String key : root.keySet()) {
            Node valueNode = ParserRegister.parse(parserType, key, root.get(key));
            if(valueNode==null) {
                valueNode = ValueNode.getValueNode(root.get(key));
            }
            nm.m.put(new StringNode(key), valueNode);
        }

        return nm;
    }
}
