package com.didiglobal.knowframework.security.common.dto.user;

import com.didiglobal.knowframework.security.common.dto.PageParamDTO;
import com.didiglobal.knowframework.security.common.dto.resource.MByUQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserBriefQueryDTO extends PageParamDTO {

    /**
     * 账户名
     */
    private String userName;

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

    public UserBriefQueryDTO(MByUQueryDTO queryDTO) {
        this.setPage(queryDTO.getPage());
        this.setSize(queryDTO.getSize());
        this.userName = queryDTO.getUserName();
        this.realName = queryDTO.getRealName();
        this.deptId = queryDTO.getDeptId();
        this.deptName = queryDTO.getDeptName();
    }
}
