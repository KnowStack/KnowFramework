package com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.query;

import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.Node;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor.basic.Visitor;

/**
 * @Author: D10865
 * @Description:
 * @Date: Create on 2018/9/14 下午3:33
 * @Modified By
 *
 * 存储boosting查询子句里的元素
 *
 */
public class Boosting extends KeyWord {

    public Node n;

    public Boosting(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
