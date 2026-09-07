package com.lony.orbiwise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lony.orbiwise.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门票 Mapper 接口
 * <p>提供门票表的基础 CRUD 操作，支持按景点查询门票</p>
 *
 * @author lin504
 */
@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {

}
