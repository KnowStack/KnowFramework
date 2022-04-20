package com.didiglobal.logi.security.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.didiglobal.logi.security.properties.LogiSecurityProper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.didiglobal.logi.security.common.constant.FieldConstant.*;

/**
 * @author cjm
 */
@Component
public class BaseDaoImpl<T> {

    @Autowired
    protected LogiSecurityProper logiSecurityProper;

    protected QueryWrapper<T> getQueryWrapperWithAppName() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(APP_NAME, logiSecurityProper.getAppName());
        queryWrapper.eq(IS_DELETE, 0);
        return queryWrapper;
    }
}
