package com.didiglobal.logi.security.common.dto.resource.type;

import com.didiglobal.logi.security.common.dto.PageParamDTO;
import com.didiglobal.logi.security.common.dto.resource.MByRQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResourceTypeQueryDTO extends PageParamDTO {

    /**
     * 资源类型名
     */
    private String typeName;

    public ResourceTypeQueryDTO(MByRQueryDTO queryVo) {
        this.setPage(queryVo.getPage());
        this.setSize(queryVo.getSize());
        this.typeName = queryVo.getName();
    }
}
