package com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor.basic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.Node;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.key.FieldNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.key.IdentityNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.key.KeyNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.key.StringNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.multi.NodeList;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.JsonNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.ObjectNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.QueryStringValueNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.StringListNode;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.visitor.QSOutputVisitor;


public class OutputVisitor extends BaseVisitor {
    public Object ret;

    @Override
    public void visit(FieldNode node) {
        ret = node.value;
    }

    @Override
    public void visit(IdentityNode node) {
        ret = node.value;
    }

    @Override
    public void visit(StringNode node) {
        ret = node.value;
    }

    @Override
    public void visit(JsonNode node) {
        ret = node.json;
    }

    @Override
    public void visit(ObjectNode node) {
        ret = node.value;
    }

    @Override
    public void visit(QueryStringValueNode node) {
        QSOutputVisitor outputVisitor = new QSOutputVisitor();
        node.getQsNode().accept(outputVisitor);

        ret = outputVisitor.output();
    }


    @Override
    public void visit(NodeMap node) {
        JSONObject root = new JSONObject();

        String key = "";
        Node valueNode = null;
        for (KeyNode n : node.m.keySet()) {

            valueNode = node.m.get(n);

            // 如果valueNode节点是关键字，则从关键字获取修改后key的值，否则遍历valueNode得到key
            if (valueNode instanceof KeyWord) {
                key = ((KeyWord) valueNode).getName();
            } else {
                n.accept(this);
                key = (String) ret;
            }

            valueNode.accept(this);
            Object value = ret;

            root.put(key, value);
        }

        this.ret = root;
    }

    @Override
    public void visit(NodeList node) {
        JSONArray array = new JSONArray();
        for (Node n : node.l) {
            if (n instanceof KeyWord) {
                JSONObject obj = new JSONObject();
                n.accept(this);
                obj.put(((KeyWord) n).getName(), ret);
                array.add(obj);

            } else {
                n.accept(this);
                array.add(ret);
            }
        }
        ret = array;
    }

    @Override
    public void visit(StringListNode node) {
        StringBuilder sb = new StringBuilder();
        for (Node n : node.l.l) {
            n.accept(this);
            sb.append(ret.toString());
        }
        ret = sb;
    }
}
