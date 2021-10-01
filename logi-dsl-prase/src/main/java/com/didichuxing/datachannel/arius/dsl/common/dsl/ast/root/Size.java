package com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ValueNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.Visitor;

public class Size extends KeyWord {

    public ValueNode v;

    public Size(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
