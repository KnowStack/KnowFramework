package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.root;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ObjectNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.FieldFormatMap;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

/**
 * @author D10865
 * 解析fieldFormatMap关键字
 * {"fieldFormatMap":"{\"_source\":{\"id\":\"_source\"}}","timeFieldName":"logTime","title":"cn_wo21072_automarket_devcon-customer.automarket*","fields":"[]"}
 */
public class FieldFormatMapParser extends DslParser {

    public FieldFormatMapParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        FieldFormatMap node = new FieldFormatMap(name);
        node.n = new ObjectNode(obj);

        return node;
    }

}
