package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.aggr;

import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.aggr.DateRange;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.aggr.ExtendedStats;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.Node;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.FieldNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ValueNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserRegister;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;
import com.didichuxing.datachannel.arius.dsl.common.dsl.util.ConstValue;

public class ExtendedStatsParser extends DslParser {

    public ExtendedStatsParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        ExtendedStats node = new ExtendedStats(name);
        NodeMap nm = new NodeMap();

        JSONObject jsonObject = (JSONObject) obj;
        for(String key : jsonObject.keySet()) {
            Node value;

            if(key.equalsIgnoreCase("field")) {
                value = new FieldNode(jsonObject.get(key));
            } else if(key.equalsIgnoreCase("script")) {
                value = ParserRegister.parse(parserType, key, jsonObject.get(key));
            } else {
                value = ValueNode.getValueNode(jsonObject.get(key));
            }

            nm.m.put(new StringNode(key), value);
        }

        node.n = nm;
        return node;
    }
}
