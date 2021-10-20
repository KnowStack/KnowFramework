package com.didiglobal.logi.elasticsearch.client.parser.sql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.didiglobal.logi.elasticsearch.client.parser.constant.SqlItemType;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.TreeSet;


public class EsExportParameterVisitor extends MySqlASTVisitorAdapter {


    private Set<String> tableNameSet = new TreeSet<>();

    private Set<String> selectFieldNameSet = new TreeSet<>();

    private Set<String> whereFieldNameSet = new TreeSet<>();

    private Set<String> termFilterFieldNameSet = new TreeSet<>();

    private Set<String> rangeFilterFieldNameSet = new TreeSet<>();

    private Set<String> groupByFieldNameSet = new TreeSet<>();

    private Set<String> sortByFieldNameSet = new TreeSet<>();

    private SqlItemType sqlItemType = SqlItemType.NONE;

    public EsExportParameterVisitor() {

    }


    @Override
    public boolean visit(SQLAllColumnExpr x) {

        setAddItem(this.selectFieldNameSet, x.toString());

        return false;
    }


    @Override
    public boolean visit(SQLSelectItem sqlSelectItem) {

        this.sqlItemType = SqlItemType.SELECT;
        sqlSelectItem.getExpr().accept(this);
        this.sqlItemType = SqlItemType.NONE;

        return false;
    }


    @Override
    public boolean visit(SQLBinaryOpExpr whereSQLExpr) {

        SqlItemType old = this.sqlItemType;


        if (whereSQLExpr.getOperator() == SQLBinaryOperator.GreaterThan
                || whereSQLExpr.getOperator() == SQLBinaryOperator.GreaterThanOrEqual
                || whereSQLExpr.getOperator() == SQLBinaryOperator.Is
                || whereSQLExpr.getOperator() == SQLBinaryOperator.LessThan
                || whereSQLExpr.getOperator() == SQLBinaryOperator.LessThanOrEqual
                || whereSQLExpr.getOperator() == SQLBinaryOperator.LessThanOrEqualOrGreaterThan
                || whereSQLExpr.getOperator() == SQLBinaryOperator.LessThanOrGreater
                || whereSQLExpr.getOperator() == SQLBinaryOperator.Like
                || whereSQLExpr.getOperator() == SQLBinaryOperator.NotLike
                || whereSQLExpr.getOperator() == SQLBinaryOperator.RLike
                || whereSQLExpr.getOperator() == SQLBinaryOperator.NotRLike
                || whereSQLExpr.getOperator() == SQLBinaryOperator.NotEqual
                || whereSQLExpr.getOperator() == SQLBinaryOperator.NotLessThan
                || whereSQLExpr.getOperator() == SQLBinaryOperator.NotGreaterThan
                || whereSQLExpr.getOperator() == SQLBinaryOperator.IsNot
                || whereSQLExpr.getOperator() == SQLBinaryOperator.Escape
                || whereSQLExpr.getOperator() == SQLBinaryOperator.RegExp
                || whereSQLExpr.getOperator() == SQLBinaryOperator.NotRegExp
                || whereSQLExpr.getOperator() == SQLBinaryOperator.Equality
        ) {


            if (whereSQLExpr.getOperator() == SQLBinaryOperator.Equality
                    || whereSQLExpr.getOperator() == SQLBinaryOperator.Is
            ) {
                this.sqlItemType = SqlItemType.WHERE_TERM;

            } else if (whereSQLExpr.getOperator() == SQLBinaryOperator.GreaterThan
                    || whereSQLExpr.getOperator() == SQLBinaryOperator.GreaterThanOrEqual
                    || whereSQLExpr.getOperator() == SQLBinaryOperator.LessThan
                    || whereSQLExpr.getOperator() == SQLBinaryOperator.LessThanOrEqual
                    || whereSQLExpr.getOperator() == SQLBinaryOperator.LessThanOrEqualOrGreaterThan
                    || whereSQLExpr.getOperator() == SQLBinaryOperator.LessThanOrGreater
                    || whereSQLExpr.getOperator() == SQLBinaryOperator.NotLessThan
                    || whereSQLExpr.getOperator() == SQLBinaryOperator.NotGreaterThan
                    || whereSQLExpr.getOperator() == SQLBinaryOperator.NotEqual
                    || whereSQLExpr.getOperator() == SQLBinaryOperator.IsNot) {

                this.sqlItemType = SqlItemType.WHERE_RANGE;

            } else {
                this.sqlItemType = SqlItemType.WHERE;
            }

            whereSQLExpr.getLeft().accept(this);

            this.sqlItemType = old;

        } else {

            this.sqlItemType = SqlItemType.WHERE;
            whereSQLExpr.getLeft().accept(this);
            whereSQLExpr.getRight().accept(this);
            this.sqlItemType = old;
        }

        return false;
    }


    @Override
    public boolean visit(SQLBetweenExpr betweenExpr) {
        SqlItemType old = this.sqlItemType;

        this.sqlItemType = SqlItemType.WHERE_RANGE;
        betweenExpr.testExpr.accept(this);

        this.sqlItemType = old;

        return false;
    }



    @Override
    public boolean visit(SQLInListExpr inListExpr) {
        SqlItemType old = this.sqlItemType;

        this.sqlItemType = SqlItemType.WHERE_TERM;
        inListExpr.getExpr().accept(this);
        for (SQLExpr sqlExpr : inListExpr.getTargetList()) {
            sqlExpr.accept(this);
        }
        this.sqlItemType = old;

        return false;
    }



    @Override
    public boolean visit(SQLSelectGroupByClause groupBySQLExpr) {

        this.sqlItemType = SqlItemType.GROUP_BY;
        for (SQLExpr sqlExpr : groupBySQLExpr.getItems()) {
            sqlExpr.accept(this);
        }
        this.sqlItemType = SqlItemType.NONE;


        SQLExpr havingSQLExpr = groupBySQLExpr.getHaving();
        if (havingSQLExpr != null) {
            this.sqlItemType = SqlItemType.WHERE;
            havingSQLExpr.accept(this);
            this.sqlItemType = SqlItemType.NONE;
        }

        return false;
    }


    @Override
    public boolean visit(SQLAggregateExpr sqlAggregateExpr) {

        this.sqlItemType = SqlItemType.GROUP_BY;
        for (SQLExpr sqlExpr : sqlAggregateExpr.getArguments()) {
            sqlExpr.accept(this);
        }
        this.sqlItemType = SqlItemType.NONE;

        return false;
    }


    @Override
    public boolean visit(SQLOrderBy orderBySQLExpr) {

        this.sqlItemType = SqlItemType.ORDER_BY;
        for (SQLSelectOrderByItem orderByItem : orderBySQLExpr.getItems()) {
            orderByItem.getExpr().accept(this);
        }
        this.sqlItemType = SqlItemType.NONE;

        return false;
    }


    @Override
    public boolean visit(SQLExprTableSource x) {
        String tableName = x.getExpr().toString();

        int index = tableName.indexOf("/");
        if (index > 0) {
            setAddItem(this.tableNameSet, tableName.substring(0, index));
        } else {
            setAddItem(this.tableNameSet, tableName);
        }

        return false;
    }


    @Override
    public boolean visit(SQLJoinTableSource x) {
        setAddItem(this.tableNameSet, x.toString());

        return false;
    }


    @Override
    public boolean visit(SQLIdentifierExpr x) {

        if (SqlItemType.SELECT == this.sqlItemType) {
            setAddItem(this.selectFieldNameSet, x.getName());

        } else if (SqlItemType.GROUP_BY == this.sqlItemType) {
            setAddItem(this.groupByFieldNameSet, x.getName());

        } else if (SqlItemType.ORDER_BY == this.sqlItemType) {
            setAddItem(this.sortByFieldNameSet, x.getName());

        } else if (SqlItemType.WHERE == this.sqlItemType) {
            setAddItem(this.whereFieldNameSet, x.getName());


        } else if (SqlItemType.WHERE_RANGE == this.sqlItemType) {
            setAddItem(this.rangeFilterFieldNameSet, x.getName());
            setAddItem(this.whereFieldNameSet, x.getName());

        } else if (SqlItemType.WHERE_TERM == this.sqlItemType) {
            setAddItem(this.termFilterFieldNameSet, x.getName());
            setAddItem(this.whereFieldNameSet, x.getName());
        }

        return false;
    }



    public String getTableName() {
        return StringUtils.join(this.tableNameSet, ",");
    }


    public String getSelectFieldNames() {
        return StringUtils.join(this.selectFieldNameSet, ",");
    }


    public String getGroupByFieldNames() {
        return StringUtils.join(this.groupByFieldNameSet, ",");
    }


    public String getOrderByFieldNames() {
        return StringUtils.join(this.sortByFieldNameSet, ",");
    }


    public String getWhereFieldsNames() {
        return StringUtils.join(this.whereFieldNameSet, ",");
    }


    public String getTermFilterFieldsNames() {
        return StringUtils.join(this.termFilterFieldNameSet, ",");
    }


    public String getRangeFilterFieldsNames() {
        return StringUtils.join(this.rangeFilterFieldNameSet, ",");
    }


    private void setAddItem(Set<String> set, String fieldName) {
        if (StringUtils.isNotBlank(fieldName)) {
            String value = fieldName.trim();
            String[] itemArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(value, ",");
            for (String item : itemArray) {
                set.add(item.trim());
            }
        }
    }

}
