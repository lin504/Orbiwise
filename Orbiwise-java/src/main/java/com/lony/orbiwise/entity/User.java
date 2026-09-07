package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * <p>对应数据库表 user，存储平台用户的基本信息</p>
 *
 * @author lin504
 */
@Data
@TableName("user")
public class User {

    /** 用户ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名，登录账号 */
    private String username;

    /** 密码，BCrypt加密存储 */
    private String password;

    /** 用户昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 邮箱地址 */
    private String email;

    /** 手机号码 */
    private String phone;

    /** 性别：0-未知，1-男，2-女 */
    private Integer gender;

    /** 账号状态：0-禁用，1-正常 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
