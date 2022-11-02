package com.didiglobal.knowframework.security.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cjm
 *
 * 部门信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "kf_security_config")
public class ConfigPO extends BasePO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 配置组
     */
    private String  valueGroup;

    /**
     * 配置项的名称
     */
    private String  valueName;

    /**
     * 配置项的值
     */
    private String  value;

    /**
     * 1 正常  2 禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String  memo;

    /**
     * 操作者
     */
    private String operator;
}
