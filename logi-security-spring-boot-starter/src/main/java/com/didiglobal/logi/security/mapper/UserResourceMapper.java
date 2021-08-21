package com.didiglobal.logi.security.mapper;

import com.didiglobal.logi.security.common.entity.UserProject;
import com.didiglobal.logi.security.common.entity.UserResource;
import com.didiglobal.logi.security.common.po.UserResourcePO;
import com.didiglobal.logi.security.mapper.EasyBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author cjm
 */
@Mapper
public interface UserResourceMapper extends EasyBaseMapper<UserResourcePO> {
}
