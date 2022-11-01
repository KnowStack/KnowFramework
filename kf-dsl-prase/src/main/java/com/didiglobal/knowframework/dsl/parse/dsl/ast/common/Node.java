package com.didiglobal.knowframework.dsl.parse.dsl.ast.common;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.key.IdentityNode;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.key.StringNode;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.value.ObjectNode;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.ParserRegister;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.ParserType;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;

public abstract  class Node {
    abstract public void accept(Visitor vistor);

    // 转化一个object,这个object只有一个key
    public static Node toNodeWith1Key(ParserType type, JSONObject root, boolean isMulti) throws Exception {
        if(root==null) {
            return new ObjectNode(root);
        }

        if (!isMulti && root.keySet().size() > 1) {
            throw new Exception("wrong json, json:" + root);
        }

        NodeMap nm = new NodeMap();
        for (String key : root.keySet()) {
            Object obj = root.get(key);

            Node node = ParserRegister.parse(type, key, obj);

            if(node==null) {
                Node tmp = toNodeWith1Key(type, (JSONObject) obj, isMulti);
                nm.m.put(new IdentityNode(key), tmp);
            } else {
                nm.m.put(new StringNode(key), node);
            }
        }

        return nm;
    }
}
