package com.lony.orbiwise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lony.orbiwise.entity.Permission;
import com.lony.orbiwise.entity.Role;
import com.lony.orbiwise.entity.RolePerm;
import com.lony.orbiwise.entity.UserRole;
import com.lony.orbiwise.mapper.PermissionMapper;
import com.lony.orbiwise.mapper.RoleMapper;
import com.lony.orbiwise.mapper.RolePermMapper;
import com.lony.orbiwise.mapper.UserRoleMapper;
import com.lony.orbiwise.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限服务实现类
 * <p>实现权限查询、角色分配、权限分配等功能</p>
 *
 * @author lin504
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermMapper rolePermMapper;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getUserPermissions(Long userId) {
        // 通过关联查询获取用户所有权限编码
        return permissionMapper.selectPermCodesByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void assignRole(Long userId, Long roleId) {
        // 检查是否已存在该角色绑定
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId)
                .eq(UserRole::getRoleId, roleId);
        Long count = userRoleMapper.selectCount(queryWrapper);

        // 不存在则新增绑定
        if (count == 0) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreateTime(LocalDateTime.now());
            userRoleMapper.insert(userRole);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermission(Long roleId, List<Long> permIds) {
        // 先删除该角色的所有权限绑定
        LambdaQueryWrapper<RolePerm> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(RolePerm::getRoleId, roleId);
        rolePermMapper.delete(deleteWrapper);

        // 批量新增新的权限绑定
        if (permIds != null && !permIds.isEmpty()) {
            for (Long permId : permIds) {
                RolePerm rolePerm = new RolePerm();
                rolePerm.setRoleId(roleId);
                rolePerm.setPermId(permId);
                rolePerm.setCreateTime(LocalDateTime.now());
                rolePermMapper.insert(rolePerm);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Role> listRoles() {
        return roleMapper.selectList(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Permission> listPermissions() {
        return permissionMapper.selectList(null);
    }

}
