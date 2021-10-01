package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.root;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.FieldNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeList;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ValueNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.Fields;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.IndexConstraints;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

/**
 * @author D10865
 *
 * fields 解析器
 */
public class IndexConstraintsParser extends DslParser {

    public IndexConstraintsParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object root) throws Exception {
        IndexConstraints node = new IndexConstraints(name);
        NodeMap nm = new NodeMap();

        JSONObject jsonObject = (JSONObject) root;
        for(String key : jsonObject.keySet()) {
            FieldNode fieldNode = new FieldNode(key);
            nm.m.put(fieldNode, ValueNode.getValueNode(jsonObject.get(key)));
        }

        node.n = nm;
        return node;
    }


}
