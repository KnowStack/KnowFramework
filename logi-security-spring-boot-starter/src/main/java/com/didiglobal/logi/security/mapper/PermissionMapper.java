package com.didiglobal.logi.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.logi.security.common.entity.Permission;
import com.didiglobal.logi.security.common.po.PermissionPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author cjm
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionPO> {
}
