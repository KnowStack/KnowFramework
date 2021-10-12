package com.didiglobal.logi.dsl.parse.dsl.ast.common.logic;

import com.didiglobal.logi.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.logi.dsl.parse.dsl.ast.common.Node;
import com.didiglobal.logi.dsl.parse.dsl.visitor.basic.Visitor;

public class Or extends KeyWord {
    public Node n;

    public Or(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
