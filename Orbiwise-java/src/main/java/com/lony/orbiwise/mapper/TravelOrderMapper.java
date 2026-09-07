package com.lony.orbiwise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lony.orbiwise.entity.TravelOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 旅游订单 Mapper 接口
 * <p>提供订单表的基础 CRUD 操作，支持按用户查询订单</p>
 *
 * @author lin504
 */
@Mapper
public interface TravelOrderMapper extends BaseMapper<TravelOrder> {

}
