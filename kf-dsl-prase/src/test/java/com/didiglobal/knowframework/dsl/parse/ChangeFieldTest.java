package com.didiglobal.knowframework.dsl.parse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.basic.OutputVisitor;
import com.didiglobal.knowframework.dsl.parse.dsl.ast.DslNode;
import com.didiglobal.knowframework.dsl.parse.dsl.parser.DslParser;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.EsDslExportParameterVisitor;
import com.didiglobal.knowframework.dsl.parse.dsl.visitor.FormatVisitor;
import com.vividsolutions.jts.util.Assert;
import org.junit.Test;

public class ChangeFieldTest {
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

    @Test
    public void testFun() throws Exception {
        Assert.isTrue(false);
    }

    @Test
    public void testFun2() throws Exception {
        Assert.isTrue(true);
    }
}
