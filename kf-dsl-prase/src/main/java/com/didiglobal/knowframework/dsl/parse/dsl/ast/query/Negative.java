package com.didiglobal.knowframework.dsl.parse.dsl.ast.query;

import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.multi.NodeMap;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.Visitor;

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
