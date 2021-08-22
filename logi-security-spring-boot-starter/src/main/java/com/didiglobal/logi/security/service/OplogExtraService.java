package com.didiglobal.logi.security.service;

import com.didiglobal.logi.security.common.entity.OplogExtra;

import java.util.List;

/**
 * @author cjm
 */
public interface OplogExtraService {

    /**
     * 操作日志信息（操作页面、操作类型、对象分类）
     * @param type 操作页面 or 操作类型 or 对象分类
     * @return 操作日志信息（操作页面、操作类型、对象分类）List
     */
    List<OplogExtra> getOplogExtraListByType(Integer type);
}
