package com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.query;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.query.GeoShape;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.DslParser;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.parser.ParserType;
import com.didiglobal.knowframework.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;

public class GeoShapeParser extends DslParser {
    public GeoShapeParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        GeoShape node = new GeoShape(name);

        NodeMap nm = new NodeMap();
        NodeMap.toField4Value((JSONObject) obj, nm);
        node.n = nm;

        return node;
    }
}
