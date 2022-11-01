package com.didiglobal.knowframework.dsl.parse.dsl.ast.common.value;

import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.multi.NodeList;

public class StringListNode extends ValueNode {
    public NodeList l = new NodeList();

    @Override
    public int hashCode() {
        return l.hashCode();
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
