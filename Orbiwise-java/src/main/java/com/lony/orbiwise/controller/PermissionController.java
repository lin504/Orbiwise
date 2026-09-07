package com.lony.orbiwise.controller;

import com.lony.orbiwise.annotation.RequirePermission;
import com.lony.orbiwise.dto.PermissionDTO;
import com.lony.orbiwise.entity.Permission;
import com.lony.orbiwise.entity.Role;
import com.lony.orbiwise.service.PermissionService;
import com.lony.orbiwise.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 * <p>提供权限查询、角色分配、权限分配接口</p>
 *
 * @author lin504
 */
@Tag(name = "权限管理", description = "权限查询、角色和权限分配接口")
@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 查询用户权限
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @Operation(summary = "查询用户权限", description = "获取指定用户的所有权限编码")
    @GetMapping("/user/{userId}")
    public Result<List<String>> getUserPermissions(@PathVariable Long userId) {
        List<String> permissions = permissionService.getUserPermissions(userId);
        return Result.success(permissions);
    }

    /**
     * 分配角色
     *
     * @param permissionDTO 权限分配参数
     * @return 操作结果
     */
    @Operation(summary = "分配角色", description = "为用户分配角色")
    @RequirePermission({"system:manage"})
    @PostMapping("/role/assign")
    public Result<Void> assignRole(@Valid @RequestBody PermissionDTO permissionDTO) {
        permissionService.assignRole(permissionDTO.getUserId(), permissionDTO.getRoleId());
        return Result.success();
    }

    /**
     * 分配权限
     *
     * @param permissionDTO 权限分配参数
     * @return 操作结果
     */
    @Operation(summary = "分配权限", description = "为角色分配权限")
    @RequirePermission({"system:manage"})
    @PostMapping("/permission/assign")
    public Result<Void> assignPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        permissionService.assignPermission(permissionDTO.getRoleId(), permissionDTO.getPermIds());
        return Result.success();
    }

    /**
     * 查询角色列表
     *
     * @return 角色列表
     */
    @Operation(summary = "角色列表", description = "查询所有角色")
    @GetMapping("/roles")
    public Result<List<Role>> listRoles() {
        List<Role> roles = permissionService.listRoles();
        return Result.success(roles);
    }

    /**
     * 查询权限列表
     *
     * @return 权限列表
     */
    @Operation(summary = "权限列表", description = "查询所有权限")
    @GetMapping("/permissions")
    public Result<List<Permission>> listPermissions() {
        List<Permission> permissions = permissionService.listPermissions();
        return Result.success(permissions);
    }

}
