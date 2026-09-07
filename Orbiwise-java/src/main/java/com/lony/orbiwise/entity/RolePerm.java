package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色-权限关联实体类
 * <p>对应数据库表 role_perm，记录角色与权限的绑定关系</p>
 *
 * @author lin504
 */
@Data
@TableName("role_perm")
public class RolePerm {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色ID */
    private Long roleId;

    /** 权限ID */
    private Long permId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
