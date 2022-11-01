package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.root;


import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.root.Type;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.key.StringNode;

/**
 * 解析type关键字
 * {"index":"cll_test_binlog_kafka_2018-09-25","type":"cll_binlog_type"}
 */
public class TypeParser extends DslParser {

    public TypeParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Type node = new Type(name);
        node.n = new StringNode(obj);

        return node;
    }

}
