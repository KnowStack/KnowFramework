package com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor;

import com.didiglobal.logi.elasticsearch.client.parser.constant.DslItemType;
import com.didiglobal.logi.elasticsearch.client.parser.constant.SqlItemType;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.aggr.Aggs;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.key.FieldNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.key.KeyNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.script.Script;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.QueryStringValueNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.query.*;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.root.FieldDataFields;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.root.Fields;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.root.Sort;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.root.Source;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor.basic.SeekVisitor;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.visitor.QSExportFieldVisitor;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.TreeSet;


public class EsDslExportParameterVisitor extends SeekVisitor {

    private boolean isOutput = false;


    private Set<String> selectFieldNameSet = new TreeSet<>();

    private Set<String> whereFieldNameSet = new TreeSet<>();

    private Set<String> termFilterFieldNameSet = new TreeSet<>();

    private Set<String> rangeFilterFieldNameSet = new TreeSet<>();

    private Set<String> groupByFieldNameSet = new TreeSet<>();

    private Set<String> sortByFieldNameSet = new TreeSet<>();


    private DslItemType dslItemType = DslItemType.NONE;

    public EsDslExportParameterVisitor() {

    }


    @Override
    public void visit(Source node) {
        dslItemType = DslItemType.SELECT;
        node.n.accept(this);
        dslItemType = DslItemType.NONE;
    }


    @Override
    public void visit(Fields node) {
        dslItemType = DslItemType.SELECT;
        node.n.accept(this);
        dslItemType = DslItemType.NONE;
    }


    @Override
    public void visit(QueryStringValueNode node) {
        QSExportFieldVisitor qsExportFieldVisitor = new QSExportFieldVisitor();
        node.getQsNode().accept(qsExportFieldVisitor);
        whereFieldNameSet.addAll(qsExportFieldVisitor.getFieldNameSet());
        isOutput = true;
    }



    @Override
    public void visit(QueryString node) {
        boolean isContainsAll = whereFieldNameSet.contains("*");
        dslItemType = DslItemType.WHERE;
        node.n.accept(this);
        dslItemType = DslItemType.NONE;

        if (!isContainsAll) {
            whereFieldNameSet.remove("*");
        }
        isOutput = true;
    }


    @Override
    public void visit(Script node) {



        whereFieldNameSet.add("*");
        isOutput = true;
    }


    @Override
    public void visit(Query node) {

        if (dslItemType == DslItemType.NONE) {

            dslItemType = DslItemType.WHERE;
            for (KeyNode n : node.m.m.keySet()) {
                n.accept(this);
                node.m.m.get(n).accept(this);
            }

            dslItemType = DslItemType.NONE;
        }
    }


    @Override
    public void visit(Term node) {
        DslItemType old = this.dslItemType;

        this.dslItemType = DslItemType.WHERE_TERM;
        node.m.accept(this);
        this.dslItemType = old;
    }


    @Override
    public void visit(Terms node) {
        DslItemType old = this.dslItemType;

        this.dslItemType = DslItemType.WHERE_TERM;
        node.m.accept(this);
        this.dslItemType = old;
    }


    @Override
    public void visit(Range node) {
        DslItemType old = this.dslItemType;

        this.dslItemType = DslItemType.WHERE_RANGE;
        node.m.accept(this);
        this.dslItemType = old;
    }


    @Override
    public void visit(Aggs node) {
        dslItemType = DslItemType.GROUP_BY;
        for (KeyNode n : node.m.m.keySet()) {
            n.accept(this);
            node.m.m.get(n).accept(this);
        }
        dslItemType = DslItemType.NONE;
    }


    @Override
    public void visit(Sort node) {
        dslItemType = DslItemType.ORDER_BY;
        node.n.accept(this);
        dslItemType = DslItemType.NONE;
    }


    @Override
    public void visit(FieldDataFields node) {
        dslItemType = DslItemType.ORDER_BY;
        node.n.accept(this);
        dslItemType = DslItemType.NONE;
    }


    @Override
    public void visit(FieldNode node) {
        if (DslItemType.SELECT == this.dslItemType) {
            setAddItem(this.selectFieldNameSet, node.value);

        } else if (DslItemType.GROUP_BY == this.dslItemType) {
            setAddItem(this.groupByFieldNameSet, node.value);

        } else if (DslItemType.ORDER_BY == this.dslItemType) {
            setAddItem(this.sortByFieldNameSet, node.value);

        } else if (DslItemType.WHERE == this.dslItemType) {
            setAddItem(this.whereFieldNameSet, node.value);


        } else if (DslItemType.WHERE_RANGE == this.dslItemType) {
            setAddItem(this.rangeFilterFieldNameSet, node.value);
            setAddItem(this.whereFieldNameSet, node.value);

        } else if (DslItemType.WHERE_TERM == this.dslItemType) {
            setAddItem(this.termFilterFieldNameSet, node.value);
            setAddItem(this.whereFieldNameSet, node.value);
        }
    }


    public String getSelectFieldNames() {

        if (this.selectFieldNameSet.isEmpty()) {
            return "*";
        }

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

    public boolean isOutput() {
        return isOutput;
    }

    public void setOutput(boolean output) {
        isOutput = output;
    }


    private void setAddItem(Set<String> set, String fieldName) {
        if (StringUtils.isNotBlank(fieldName)) {
            String value = fieldName.trim();
            String[] itemArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(value, ",");
            for (String item : itemArray) {
                set.add(formatField(item));
            }
        }
    }


    private String formatField(String field) {
        if (StringUtils.isBlank(field)) {
            return field;
        }

        field = field.trim();


        field = field.replaceAll("\\\\", "");
        field = field.replaceAll("\"", "");

        return field;
    }

}
