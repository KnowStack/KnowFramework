package com.didiglobal.logi.elasticsearch.client.parser.sql;

import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.didiglobal.logi.elasticsearch.client.parser.bean.DangerousDslTagEnum;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;


public class SqlDangerousTagVisitor extends MySqlOutputVisitor {

    private static final ILog LOGGER = LogFactory.getLog(SqlDangerousTagVisitor.class);

    private Set<String> tags = Sets.newHashSet();

    private int aggsLevel = 0;

    private StringBuilder stringBuilder;

    public SqlDangerousTagVisitor(StringBuilder sb) {
        super(sb);
        this.stringBuilder = sb;
    }


    @Override
    public boolean visit(SQLSelectGroupByClause groupBySQLExpr) {
        this.aggsLevel += groupBySQLExpr.getItems().size();

        return super.visit(groupBySQLExpr);
    }


    @Override
    public boolean visit(SQLAggregateExpr sqlAggregateExpr) {
        this.aggsLevel += sqlAggregateExpr.getArguments().size();

        if ("COUNT".equals(sqlAggregateExpr.getMethodName())) {
            if (sqlAggregateExpr.getOption() != null && sqlAggregateExpr.getOption() instanceof SQLAggregateOption) {
                if ("DISTINCT".equals(((SQLAggregateOption)(sqlAggregateExpr.getOption())).name())) {
                    this.tags.add(DangerousDslTagEnum.AGGS_CARDINALITY.getTag());
                }
            }
        }

        return super.visit(sqlAggregateExpr);
    }


    @Override
    public boolean visit(SQLBinaryOpExpr x) {


        if (x.getOperator() == SQLBinaryOperator.Like
                ) {

            if (x.getRight() instanceof SQLCharExpr) {
                String value = x.getRight().toString();
                if (StringUtils.isNotBlank(value) && value.length() > 2) {
                    if (value.charAt(1) == '%' || value.charAt(1) == '*') {
                        this.tags.add(DangerousDslTagEnum.WITH_WILDCARD_PRE.getTag());
                    }
                }
            }

            return super.visit(x);
        }

        return super.visit(x);
    }


    @Override
    public boolean visit(SQLMethodInvokeExpr x) {

        if ("script".equals(x.getMethodName())) {
            this.tags.add(DangerousDslTagEnum.WITH_SCRIPT.getTag());

        } else if ("regexp".equals(x.getMethodName())) {
            this.tags.add(DangerousDslTagEnum.WITH_REGEXP.getTag());
        }

        return super.visit(x);
    }


    public Set<String> getDangerousTags() {


        if (this.stringBuilder.length() > 5 * 1024) {

            this.tags.add(DangerousDslTagEnum.DSL_LENGTH_TOO_LARGE.getTag());
        }

        if (this.aggsLevel >= 3) {
            this.tags.add(DangerousDslTagEnum.AGGS_DEEP_NEST.getTag());
        }

        return tags;
    }


    public String getSql() {
        return stringBuilder.toString();
    }

}
