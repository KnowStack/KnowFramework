package com.didiglobal.logi.elasticsearch.client.parser.constant;

/**
 * @author D10865
 *
 * 查询节点类型
 */
public enum SqlItemType {
    SELECT,
    WHERE,
    WHERE_TERM,
    WHERE_RANGE,
    GROUP_BY,
    ORDER_BY,
    HAVING,
    NONE
}