package com.didiglobal.logi.security.common.vo.resource;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cjm
 *
 * 资源权限管理>按资源管理的列表信息
 *
 * MByR（ManageByResource）
 */
@Data
@ApiModel(description = "资源权限管理（按资源管理的列表信息）")
public class MByRVo {

    /**
     * 管理权限用户数
     */
    @ApiModelProperty(value = "管理权限用户数", dataType = "Integer", required = false)
    private Integer adminUserCnt;

    /**
     * 查看权限用户数
     */
    @ApiModelProperty(value = "查看权限用户数", dataType = "Integer", required = false)
    private Integer viewUserCnt;

    /**
     * 附加属性（两个key-value），key属性名，value属性值
     * key的解释：
     * key1：列表字段1（如果是全部项目级别：项目ID、如果是具体项目级别：资源类型、如果是资源类别级别：资源名称）
     * key2：列表字段2（如果是全部项目级别：项目名称、如果是具体项目级别：归属项目、如果是资源类别级别：资源类型）
     */
    @JsonIgnore
    private Map<String, String> otherAttribute = new HashMap<>();

    @JsonAnyGetter
    public Map<String, String> otherAttribute() {
        return otherAttribute;
    }
}
