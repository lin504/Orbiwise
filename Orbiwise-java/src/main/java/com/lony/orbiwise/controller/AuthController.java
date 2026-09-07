package com.lony.orbiwise.controller;

import com.lony.orbiwise.dto.LoginDTO;
import com.lony.orbiwise.entity.User;
import com.lony.orbiwise.service.UserService;
import com.lony.orbiwise.util.Result;
import com.lony.orbiwise.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * <p>处理用户登录和注册请求</p>
 *
 * @author lin504
 */
@Tag(name = "认证管理", description = "用户登录注册接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param loginDTO 登录参数
     * @return 登录结果（含token和用户信息）
     */
    @Operation(summary = "用户登录", description = "通过用户名密码登录，返回JWT Token")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册结果
     */
    @Operation(summary = "用户注册", description = "注册新用户，密码BCrypt加密存储")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

}
