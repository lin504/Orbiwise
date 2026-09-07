package com.lony.orbiwise.service;

import com.lony.orbiwise.entity.Permission;
import com.lony.orbiwise.entity.Role;

import java.util.List;

/**
 * 权限服务接口
 * <p>提供权限查询、角色分配、权限分配等功能</p>
 *
 * @author lin504
 */
public interface PermissionService {

    /**
     * 获取用户所有权限编码
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 分配角色给用户
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    void assignRole(Long userId, Long roleId);

    /**
     * 分配权限给角色
     *
     * @param roleId  角色ID
     * @param permIds 权限ID列表
     */
    void assignPermission(Long roleId, List<Long> permIds);

    /**
     * 查询所有角色列表
     *
     * @return 角色列表
     */
    List<Role> listRoles();

    /**
     * 查询所有权限列表
     *
     * @return 权限列表
     */
    List<Permission> listPermissions();

}
