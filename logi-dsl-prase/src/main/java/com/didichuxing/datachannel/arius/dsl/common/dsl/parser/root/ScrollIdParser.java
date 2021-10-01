package com.didichuxing.datachannel.arius.dsl.common.dsl.parser.root;

import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.KeyWord;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.common.value.ObjectNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.root.ScrollId;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.ParserType;

/**
 * @author D10865
 * 解析scroll_id关键字  或 scrollId关键字
 * {"scroll":"60s","scroll_id":"cXVlcnlBbmRGZXRjaDsxOzEyNjEzOTpXYW9YS2dlQVM1YU9hZFJXVFNZa2x3OzA7"}
 * {"scroll":"1m","scrollId":"cXVlcnlUaGVuRmV0Y2g7ODsyMjcwNTg5MzoyWF"}
 */
public class ScrollIdParser extends DslParser {

    public ScrollIdParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        ScrollId node = new ScrollId(name);
        node.n = new ObjectNode(obj);

        return node;
    }

}
