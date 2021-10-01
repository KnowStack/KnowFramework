package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.root;


import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.Type;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

/**
 * @author D10865
 *
 * 解析type关键字
 *
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
