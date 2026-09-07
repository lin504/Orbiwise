package com.lony.orbiwise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lony.orbiwise.entity.Strategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * 旅游攻略 Mapper 接口
 * <p>提供攻略表的基础 CRUD 操作，支持分页查询和全文搜索</p>
 *
 * @author lin504
 */
@Mapper
public interface StrategyMapper extends BaseMapper<Strategy> {

}
