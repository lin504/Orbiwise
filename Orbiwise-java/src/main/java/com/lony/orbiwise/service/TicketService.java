package com.lony.orbiwise.service;

import com.lony.orbiwise.dto.TicketDTO;
import com.lony.orbiwise.entity.Ticket;

import java.util.List;

/**
 * 门票服务接口
 * <p>提供门票的查询和管理功能</p>
 *
 * @author lin504
 */
public interface TicketService {

    /**
     * 查询景点门票列表
     *
     * @param scenicId 景点ID
     * @return 门票列表
     */
    List<Ticket> listByScenicId(Long scenicId);

    /**
     * 新增门票
     *
     * @param ticketDTO 门票参数
     */
    void createTicket(TicketDTO ticketDTO);

    /**
     * 编辑门票
     *
     * @param id        门票ID
     * @param ticketDTO 门票参数
     */
    void updateTicket(Long id, TicketDTO ticketDTO);

}
