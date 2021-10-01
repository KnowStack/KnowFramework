package com.didichuxing.datachannel.arius.dsl.common.dsl.ast.query;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.Visitor;

/**
 * @author D10865
 *
 * 存储negative查询子句里的元素
 */
public class Negative extends KeyWord {

    public NodeMap m = new NodeMap();

    public Negative(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
