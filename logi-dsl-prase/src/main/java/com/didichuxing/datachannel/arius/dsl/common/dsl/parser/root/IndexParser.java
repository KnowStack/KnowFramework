package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.root;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ObjectNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.Index;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

/**
 * @author D10865
 *
 * 解析index关键字
 *
 * {"index":["arius_dsl_log_2018-09-20"],"ignore_unavailable":true}
 */
public class IndexParser extends DslParser {

    public IndexParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Index node = new Index(name);
        node.n = new ObjectNode(obj);

        return node;
    }

}
