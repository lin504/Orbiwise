package com.lony.orbiwise.vo;

import lombok.Data;

import java.util.List;

/**
 * 登录响应VO
 * <p>登录成功后返回的token和用户基本信息</p>
 *
 * @author lin504
 */
@Data
public class LoginVO {

    /** JWT令牌 */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 角色列表 */
    private List<String> roles;

}
