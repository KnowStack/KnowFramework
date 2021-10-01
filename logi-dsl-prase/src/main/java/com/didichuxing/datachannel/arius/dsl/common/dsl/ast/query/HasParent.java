package com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.Visitor;

public class HasParent extends KeyWord {
    public static final String QUERY_STR = "query";

    public NodeMap m = new NodeMap();


    public HasParent(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor vistor) {
        vistor.visit(this);
    }
}
