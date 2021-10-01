package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.script;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.multi.NodeMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.script.Script;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ValueNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

// TODO  支持脚本
public class ScriptParser extends DslParser {

    public ScriptParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Script node = new Script(name);

        if(!(obj instanceof JSON)) {
            node.n = ValueNode.getValueNode(obj);
            return node;
        }

        NodeMap nm = new NodeMap();
        NodeMap.toString2ValueWithField(parserType, (JSONObject) obj, nm, null);
        node.n = nm;

        return node;
    }
}