package com.didiglobal.knowframework.dsl.parse.dsl.ast.aggr;

import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;

public class AvgBucket extends KeyWord {

    public NodeMap m = new NodeMap();

    public AvgBucket(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }



}
