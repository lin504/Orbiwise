package com.lony.orbiwise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lony.orbiwise.dto.LoginDTO;
import com.lony.orbiwise.entity.User;
import com.lony.orbiwise.exception.BusinessException;
import com.lony.orbiwise.mapper.PermissionMapper;
import com.lony.orbiwise.mapper.UserMapper;
import com.lony.orbiwise.service.UserService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.JwtUtil;
import com.lony.orbiwise.util.ResultCode;
import com.lony.orbiwise.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 * <p>实现用户登录、注册、信息查询等功能</p>
 *
 * @author lin504
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 根据用户名查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        // 验证用户是否存在
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 验证密码是否匹配（BCrypt）
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 验证用户状态是否正常
        if (user.getStatus() != Constants.USER_STATUS_NORMAL) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        // 查询用户角色列表
        List<String> roles = permissionMapper.selectRoleNamesByUserId(user.getId());

        // 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);

        // 构建登录响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        loginVO.setRoles(roles);

        return loginVO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        // 清除密码信息，避免泄露
        user.setPassword(null);
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 密码 BCrypt 加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认值
        user.setStatus(Constants.USER_STATUS_NORMAL);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 插入用户记录
        userMapper.insert(user);
    }

}
