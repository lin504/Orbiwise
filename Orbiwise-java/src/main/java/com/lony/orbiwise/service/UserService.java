package com.lony.orbiwise.service;

import com.lony.orbiwise.dto.LoginDTO;
import com.lony.orbiwise.entity.User;
import com.lony.orbiwise.vo.LoginVO;

/**
 * 用户服务接口
 * <p>提供用户登录、注册、信息查询等功能</p>
 *
 * @author lin504
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录参数
     * @return 登录响应（含token和用户信息）
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户实体
     */
    User getUserInfo(Long userId);

    /**
     * 用户注册
     *
     * @param user 用户信息（含用户名、密码、昵称等）
     */
    void register(User user);

}
