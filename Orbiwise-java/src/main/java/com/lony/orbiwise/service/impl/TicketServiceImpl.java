package com.lony.orbiwise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lony.orbiwise.dto.TicketDTO;
import com.lony.orbiwise.entity.Ticket;
import com.lony.orbiwise.exception.BusinessException;
import com.lony.orbiwise.mapper.TicketMapper;
import com.lony.orbiwise.service.TicketService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.ResultCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 门票服务实现类
 * <p>实现门票的查询和管理功能</p>
 *
 * @author lin504
 */
@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketMapper ticketMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ticket> listByScenicId(Long scenicId) {
        LambdaQueryWrapper<Ticket> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Ticket::getScenicId, scenicId)
                .eq(Ticket::getStatus, Constants.SCENIC_STATUS_ON)
                .orderByAsc(Ticket::getPrice);
        return ticketMapper.selectList(queryWrapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createTicket(TicketDTO ticketDTO) {
        Ticket ticket = new Ticket();
        BeanUtils.copyProperties(ticketDTO, ticket);
        ticket.setSoldCount(0);
        ticket.setCreateTime(LocalDateTime.now());
        ticket.setUpdateTime(LocalDateTime.now());
        ticketMapper.insert(ticket);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTicket(Long id, TicketDTO ticketDTO) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "门票不存在");
        }
        BeanUtils.copyProperties(ticketDTO, ticket);
        ticket.setUpdateTime(LocalDateTime.now());
        ticketMapper.updateById(ticket);
    }

}
