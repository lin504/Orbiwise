package com.lony.orbiwise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lony.orbiwise.entity.RolePerm;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色-权限关联 Mapper 接口
 *
 * @author lin504
 */
@Mapper
public interface RolePermMapper extends BaseMapper<RolePerm> {

}
