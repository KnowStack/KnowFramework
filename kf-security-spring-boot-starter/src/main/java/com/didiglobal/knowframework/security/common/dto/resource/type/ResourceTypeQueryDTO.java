package com.didiglobal.knowframework.security.common.dto.resource.type;

import com.didiglobal.knowframework.security.common.dto.PageParamDTO;
import com.didiglobal.knowframework.security.common.dto.resource.MByRQueryDTO;
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

    public ResourceTypeQueryDTO(MByRQueryDTO queryDTO) {
        this.setPage(queryDTO.getPage());
        this.setSize(queryDTO.getSize());
        this.typeName = queryDTO.getName();
    }
}
