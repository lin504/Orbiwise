package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体类
 * <p>对应数据库表 role，定义系统角色（如管理员、普通用户等）</p>
 *
 * @author lin504
 */
@Data
@TableName("role")
public class Role {

    /** 角色ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色名称，如"管理员"、"普通用户" */
    private String roleName;

    /** 角色描述 */
    private String roleDesc;

    /** 角色状态：0-禁用，1-启用 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
