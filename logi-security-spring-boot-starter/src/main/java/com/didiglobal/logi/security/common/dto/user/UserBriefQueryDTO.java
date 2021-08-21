package com.didiglobal.logi.security.common.dto.user;

import com.didiglobal.logi.security.common.dto.PageParamDTO;
import com.didiglobal.logi.security.common.dto.resource.MByUQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserBriefQueryDTO extends PageParamDTO {

    /**
     * 账户名
     */
    private String username;

    /**
     * 实名
     */
    private String realName;

    /**
     * 部门id
     */
    private Integer deptId;

    /**
     * 部门名
     */
    private String deptName;

    public UserBriefQueryDTO(MByUQueryDTO queryVo) {
        this.setPage(queryVo.getPage());
        this.setSize(queryVo.getSize());
        this.username = queryVo.getUsername();
        this.realName = queryVo.getRealName();
        this.deptId = queryVo.getDeptId();
        this.deptName = queryVo.getDeptName();
    }
}
