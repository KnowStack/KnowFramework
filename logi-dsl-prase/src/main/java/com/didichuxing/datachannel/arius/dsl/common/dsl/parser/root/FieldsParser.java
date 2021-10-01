package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.root;

import com.alibaba.fastjson.JSONArray;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.key.StringNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeList;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.Fields;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

/**
 * @author D10865
 *
 * fields 解析器
 */
public class FieldsParser extends DslParser {

    public FieldsParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object root) throws Exception {
        Fields node = new Fields(name);

        if(root instanceof JSONArray) {
            node.n = new NodeList();
            NodeList.toFieldList((JSONArray) root, (NodeList) node.n);

        } else if (root instanceof String) {
            node.n = new StringNode(root);
        }

        return node;
    }


}
