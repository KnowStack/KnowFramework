package com.didichuxing.datachannel.arius.dsl.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.arius.dsl.common.dsl.ast.DslNode;
import com.didichuxing.datachannel.arius.dsl.common.dsl.parser.DslParser;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.EsDslExportParameterVisitor;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.FormatVisitor;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.OutputJsonVisitor;
import com.didichuxing.datachannel.arius.dsl.common.dsl.visitor.basic.OutputVisitor;
import org.junit.Test;

public class ChangeField {
    @Test
    public void testMatch() throws Exception {
        String dslContent = "{\"highlight\":{\"highlight_query\":{\"bool\":{\"must\":{\"bool\":{\"should\":{\"match\":{\"question\":{\"query\":\"route\",\"type\":\"boolean\"}}}}},\"filter\":[{\"term\":{\"channel_id\":\"2\"}},{\"term\":{\"canonical_country_code\":\"BR\"}},{\"term\":{\"organization_id\":\"1\"}}]}},\"fields\":{\"question\":{}}}}";

        JSONObject jsonObject = JSON.parseObject(dslContent);
        DslNode node = DslParser.parse(jsonObject);
        FormatVisitor formatVisitor = new FormatVisitor();
        node.accept(formatVisitor);

        OutputVisitor outputVisitor = new TransformVisitor();
        node.accept(outputVisitor);

        System.out.println(outputVisitor.ret.toString());

        EsDslExportParameterVisitor esDslExportParameterVisitor = new EsDslExportParameterVisitor();
        node.accept(esDslExportParameterVisitor);

        String dslTemplate = formatVisitor.ret.toString();
        System.out.println(dslTemplate);
    }
}
