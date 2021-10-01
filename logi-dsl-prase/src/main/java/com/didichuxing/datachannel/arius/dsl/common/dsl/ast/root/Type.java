package com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root;


import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.Node;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.Visitor;

/**
 * @author D10865
 *
 * 存储type关键字的结果
 *  {"index":"cll_test_binlog_kafka_2018-09-25","type":"cll_binlog_type"}
 *
 */
public class Type extends KeyWord {
    public Node n;

    public Type(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
