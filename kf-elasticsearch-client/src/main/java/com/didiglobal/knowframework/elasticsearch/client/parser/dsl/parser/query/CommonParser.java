package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.query.Common;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;

/**
 * 解析common子查询
 * {
 * "query": {
 * "common": {
 * "body": {
 * "query": "this is bonsai cool",
 * "cutoff_frequency": 0.001
 * }
 * }
 * }
 * }
 */
public class CommonParser extends DslParser {

    public CommonParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        // 构造Common对象
        Common node = new Common(name);
        NodeMap nm = new NodeMap();
        node.n = nm;
        NodeMap.toField4Value((JSONObject) obj, nm);

        return node;
    }

}
