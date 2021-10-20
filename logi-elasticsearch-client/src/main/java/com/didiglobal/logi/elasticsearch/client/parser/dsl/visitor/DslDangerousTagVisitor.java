package com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor;

import com.alibaba.fastjson.JSONObject;
import com.didiglobal.logi.elasticsearch.client.parser.bean.DangerousDslTagEnum;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.aggr.Aggs;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.aggr.Cardinality;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.aggr.SignificantTerms;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.Node;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.key.KeyNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.script.Script;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.JsonNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.query.Regexp;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.query.Wildcard;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor.basic.OutputVisitor;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;


public class DslDangerousTagVisitor extends OutputVisitor {

    private static final ILog LOGGER = LogFactory.getLog(DslDangerousTagVisitor.class);

    private Set<String> tags = Sets.newHashSet();

    private int aggsLevel = 0;

    private int maxAggsLevel = 0;


    @Override
    public void visit(Script node) {
        super.visit(node);
        this.tags.add(DangerousDslTagEnum.WITH_SCRIPT.getTag());
    }


    @Override
    public void visit(Wildcard node) {

        super.visit(node);


        Node valueNode = null;
        String value = null;
        for (KeyNode n : node.m.m.keySet()) {

            valueNode = node.m.m.get(n);


            if (valueNode != null && valueNode instanceof JsonNode) {

                JSONObject jsonObject = (JSONObject)(((JsonNode)valueNode).json);

                value = jsonObject.getString("value");
                if (StringUtils.isBlank(value)) {
                    value = jsonObject.getString("wildcard");
                }

                if (StringUtils.isNotBlank(value) && value.startsWith("*")) {
                    this.tags.add(DangerousDslTagEnum.WITH_WILDCARD_PRE.getTag());
                    return;
                }
            }
        }
    }


    @Override
    public void visit(Regexp node) {
        super.visit(node);
        this.tags.add(DangerousDslTagEnum.WITH_REGEXP.getTag());
    }


    @Override
    public void visit(Cardinality node) {
        super.visit(node);
        this.tags.add(DangerousDslTagEnum.AGGS_CARDINALITY.getTag());
    }


    @Override
    public void visit(SignificantTerms node) {
        super.visit(node);
        this.tags.add(DangerousDslTagEnum.AGGS_SIGNIFICANT_TERMS.getTag());
    }


    @Override
    public void visit(Aggs node) {
        ++this.aggsLevel;


        this.maxAggsLevel = Math.max(this.maxAggsLevel, this.aggsLevel);

        super.visit(node);

        --this.aggsLevel;
    }


    public Set<String> getDangerousTags() {


        if (super.ret.toString().length() > 5 * 1024) {

            this.tags.add(DangerousDslTagEnum.DSL_LENGTH_TOO_LARGE.getTag());
        }

        if (this.maxAggsLevel >= 3) {
            this.tags.add(DangerousDslTagEnum.AGGS_DEEP_NEST.getTag());
        }

        return tags;
    }

}
