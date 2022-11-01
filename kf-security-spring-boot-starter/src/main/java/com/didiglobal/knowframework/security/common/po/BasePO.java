package com.didiglobal.knowframework.security.common.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author cjm
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BasePO extends AppBasePO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic(value = "0", delval = "1")
    private int isDelete = 0;
}
