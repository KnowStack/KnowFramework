package com.didiglobal.logi.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.logi.security.common.entity.Project;
import com.didiglobal.logi.security.common.entity.ResourceType;
import com.didiglobal.logi.security.common.po.ResourceTypePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author cjm
 */
@Mapper
public interface ResourceTypeMapper extends BaseMapper<ResourceTypePO> {
}
