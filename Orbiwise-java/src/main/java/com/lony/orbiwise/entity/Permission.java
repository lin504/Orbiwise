package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限实体类
 * <p>对应数据库表 permission，定义系统权限项</p>
 *
 * @author lin504
 */
@Data
@TableName("permission")
public class Permission {

    /** 权限ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 权限名称，如"景点管理" */
    private String permName;

    /** 权限编码，如"scenic:manage"，用于注解校验 */
    private String permCode;

    /** 权限描述 */
    private String permDesc;

    /** 所属模块，如"scenic"、"order" */
    private String module;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
