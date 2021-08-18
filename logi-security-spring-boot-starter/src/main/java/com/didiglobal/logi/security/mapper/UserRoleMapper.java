package com.didiglobal.logi.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.logi.security.common.entity.UserRole;
import com.didiglobal.logi.security.common.po.UserRolePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author cjm
 */
@Mapper
public interface UserRoleMapper extends EasyBaseMapper<UserRolePO> {
}
