package com.didiglobal.knowframework.dsl.parse.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.Node;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.query.HasParent;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.key.KeyNode;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.key.StringNode;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.value.ValueNode;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.DslParser;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.ParserRegister;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.ParserType;

public class HasParentParser extends DslParser {
    public HasParentParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        HasParent node = new HasParent(name);

        JSONObject jsonObject = (JSONObject) obj;
        for(String key : jsonObject.keySet()) {
            Object o = jsonObject.get(key);

            KeyNode keyNode = new StringNode(key);

            Node valueNode = ParserRegister.parse(parserType, key, o);
            if(valueNode==null) {
                valueNode = ValueNode.getValueNode(o);
            }
            node.m.m.put(keyNode, valueNode);
        }

        return node;
    }
}
