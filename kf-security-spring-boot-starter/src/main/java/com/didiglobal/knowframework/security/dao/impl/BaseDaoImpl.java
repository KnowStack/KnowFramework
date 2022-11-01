package com.didiglobal.knowframework.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.knowframework.security.common.constant.FieldConstant;
import com.didiglobal.knowframework.security.properties.LogiSecurityProper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cjm
 */
@Component
public class BaseDaoImpl<T> {

    @Autowired
    protected LogiSecurityProper logiSecurityProper;

    protected QueryWrapper<T> getQueryWrapperWithAppName() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(FieldConstant.APP_NAME, logiSecurityProper.getAppName());
        return queryWrapper;
    }
}
