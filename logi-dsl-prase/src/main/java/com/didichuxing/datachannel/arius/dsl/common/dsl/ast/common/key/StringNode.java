package com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key;

import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.Visitor;

public class StringNode extends KeyNode {

    public StringNode(Object obj) {
        super(obj);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
