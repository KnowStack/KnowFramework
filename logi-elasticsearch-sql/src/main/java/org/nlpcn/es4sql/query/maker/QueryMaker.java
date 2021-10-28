package org.nlpcn.es4sql.query.maker;


import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.nlpcn.es4sql.domain.Condition;
import org.nlpcn.es4sql.domain.Where;
import org.nlpcn.es4sql.domain.Where.CONN;
import org.nlpcn.es4sql.exception.SqlParseException;

public class QueryMaker extends Maker {

    /**
     * 将where条件构建成query
     *
     * @param where
     * @return
     * @throws SqlParseException
     */
    public static BoolQueryBuilder explan(Where where) throws SqlParseException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        while (where.getWheres().size() == 1) {
            where = where.getWheres().getFirst();
        }
        new QueryMaker().explanWhere(boolQuery, where);
        return boolQuery;
    }

    private QueryMaker() {
        super(true);
    }

    private void explanWhere(BoolQueryBuilder boolQuery, Where where) throws SqlParseException {
        if (where instanceof Condition) {
            addSubQuery(boolQuery, where, (QueryBuilder) make((Condition) where));
        } else {
            BoolQueryBuilder subQuery = QueryBuilders.boolQuery();
            addSubQuery(boolQuery, where, subQuery);
            for (Where subWhere : where.getWheres()) {
                explanWhere(subQuery, subWhere);
            }
        }
    }

    /**
     * 增加嵌套插
     *
     * @param boolQuery
     * @param where
     * @param subQuery
     */
    private void addSubQuery(BoolQueryBuilder boolQuery, Where where, QueryBuilder subQuery) {
        if (where instanceof Condition) {
            Condition condition = (Condition) where;

            if (condition.isNested()) {
                subQuery = QueryBuilders.nestedQuery(condition.getNestedPath(), subQuery);
            } else if (condition.isChildren()) {
                subQuery = QueryBuilders.hasChildQuery(condition.getChildType(), subQuery);
            }
        }

        if (where.getConn() == CONN.AND) {
            boolQuery.must(subQuery);
        } else {
            boolQuery.should(subQuery);
        }
    }
}
