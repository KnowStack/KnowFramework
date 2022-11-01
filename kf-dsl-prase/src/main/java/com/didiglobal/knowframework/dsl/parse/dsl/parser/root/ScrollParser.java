package com.didiglobal.knowframework.dsl.parse.dsl.parser.root;

import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.KeyWord;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.root.Scroll;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.common.value.ObjectNode;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.DslParser;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.ParserType;

/**
 * @author D10865
 *
 * 解析scroll关键字
 *
 * {"scroll":"60s","scroll_id":"cXVlcnlBbmRGZXRjaDsxOzEyNjEzOTpXYW9YS2dlQVM1YU9hZFJXVFNZa2x3OzA7"}
 */
public class ScrollParser extends DslParser {

    public ScrollParser(ParserType type) {
        super(type);
    }

    @Override
    public KeyWord parse(String name, Object obj) throws Exception {
        Scroll node = new Scroll(name);
        node.n = new ObjectNode(obj);

        return node;
    }

}
