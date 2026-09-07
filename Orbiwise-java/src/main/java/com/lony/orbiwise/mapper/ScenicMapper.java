package com.lony.orbiwise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lony.orbiwise.entity.Scenic;
import org.apache.ibatis.annotations.Mapper;

/**
 * 景点 Mapper 接口
 * <p>提供景点表的基础 CRUD 操作，支持分页查询和全文搜索</p>
 *
 * @author lin504
 */
@Mapper
public interface ScenicMapper extends BaseMapper<Scenic> {

}
