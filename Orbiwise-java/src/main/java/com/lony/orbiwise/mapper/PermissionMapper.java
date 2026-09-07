package com.lony.orbiwise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lony.orbiwise.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限 Mapper 接口
 * <p>提供权限表的基础 CRUD 操作，支持按用户ID查询权限编码列表</p>
 *
 * @author lin504
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据用户ID查询权限编码列表
     * <p>通过关联 user_role、role_perm、permission 三张表，
     * 查询指定用户拥有的所有权限编码</p>
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @Select("SELECT DISTINCT p.perm_code FROM permission p " +
            "INNER JOIN role_perm rp ON p.id = rp.perm_id " +
            "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectPermCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色名称列表
     *
     * @param userId 用户ID
     * @return 角色名称列表
     */
    @Select("SELECT r.role_name FROM role r " +
            "INNER JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectRoleNamesByUserId(@Param("userId") Long userId);

}
