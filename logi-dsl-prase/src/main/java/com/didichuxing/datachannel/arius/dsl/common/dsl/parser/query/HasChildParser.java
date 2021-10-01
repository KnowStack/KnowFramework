package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.KeyNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ValueNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.HasChild;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query.HasParent;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

public class HasChildParser extends DslParser {
    public HasChildParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        HasChild node = new HasChild(name);
        NodeMap nm = new NodeMap();

        JSONObject jsonObject = (JSONObject) obj;
        for(String key : jsonObject.keySet()) {
            Object o = jsonObject.get(key);

            KeyNode keyNode = new StringNode(key);

            if(key.equalsIgnoreCase(HasParent.QUERY_STR)) {
                NodeMap tmp = new NodeMap();
                NodeMap.toString2Node(parserType, (JSONObject)o, tmp);
                nm.m.put(keyNode, tmp);
            } else {
                nm.m.put(keyNode, ValueNode.getValueNode(jsonObject.get(key)));
            }
        }

        node.n = nm;
        return node;
    }
}
