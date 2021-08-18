package com.didiglobal.logi.security.extend;

import com.didiglobal.logi.security.common.dto2.OplogDto;

/**
 * @author cjm
 *
 * 操作日志扩展接口
 */
public interface OplogExtend {

    /**
     * 保存操作日志
     * @param oplogDto 操作日志
     */
    void saveOplog(OplogDto oplogDto);
}
