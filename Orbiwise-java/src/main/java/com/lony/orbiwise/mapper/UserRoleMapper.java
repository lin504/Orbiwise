package com.lony.orbiwise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lony.orbiwise.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户-角色关联 Mapper 接口
 *
 * @author lin504
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}
