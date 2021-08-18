package com.didiglobal.logi.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.logi.security.common.entity.Role;
import com.didiglobal.logi.security.common.po.RolePO;
import org.apache.ibatis.annotations.*;

/**
 * @author cjm
 */
@Mapper
public interface RoleMapper extends BaseMapper<RolePO> {
}
