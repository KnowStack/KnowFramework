package com.didichuxing.datachannel.arius.dsl.common.dsl.ast;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.Node;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.KeyNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.Visitor;

import java.util.HashMap;
import java.util.Map;

public class DslNode extends Node {
    public NodeMap m = new NodeMap();

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }

}
