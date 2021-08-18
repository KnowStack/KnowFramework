package com.didiglobal.logi.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.logi.security.common.entity.RolePermission;
import com.didiglobal.logi.security.common.po.RolePermissionPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author cjm
 */
@Mapper
public interface RolePermissionMapper extends EasyBaseMapper<RolePermissionPO> {
}
