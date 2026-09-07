package com.lony.orbiwise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lony.orbiwise.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 * <p>提供用户表的基础 CRUD 操作</p>
 *
 * @author lin504
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
