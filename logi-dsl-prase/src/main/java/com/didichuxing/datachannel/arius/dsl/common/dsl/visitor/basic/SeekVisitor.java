package com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.Node;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.FieldNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.IdentityNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.KeyNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeList;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.JsonNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ObjectNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.StringListNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.Fields;


public class SeekVisitor extends BaseVisitor {

    @Override
    public void visit(FieldNode node) { }

    @Override
    public void visit(IdentityNode node) { }

    @Override
    public void visit(StringNode node) { }

    @Override
    public void visit(JsonNode node) { }

    @Override
    public void visit(ObjectNode node) { }

    @Override
    public void visit(StringListNode node) {
        node.l.accept(this);
    }

    @Override
    public void visit(NodeMap node) {
        for(KeyNode key : node.m.keySet()) {
            key.accept(this);

            node.m.get(key).accept(this);
        }
    }

    @Override
    public void visit(NodeList node) {
        for(Node n : node.l) {
            n.accept(this);
        }
    }
}
