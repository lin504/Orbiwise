package com.lony.orbiwise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lony.orbiwise.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户行为 Mapper 接口
 * <p>提供用户行为表的基础 CRUD 操作，支持按用户和行为类型查询（推荐算法用）</p>
 *
 * @author lin504
 */
@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {

}
