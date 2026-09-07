package com.lony.orbiwise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lony.orbiwise.dto.OrderCreateDTO;
import com.lony.orbiwise.entity.Scenic;
import com.lony.orbiwise.entity.Ticket;
import com.lony.orbiwise.entity.TravelOrder;
import com.lony.orbiwise.entity.UserBehavior;
import com.lony.orbiwise.exception.BusinessException;
import com.lony.orbiwise.mapper.ScenicMapper;
import com.lony.orbiwise.mapper.TicketMapper;
import com.lony.orbiwise.mapper.TravelOrderMapper;
import com.lony.orbiwise.mapper.UserBehaviorMapper;
import com.lony.orbiwise.service.OrderService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.ResultCode;
import com.lony.orbiwise.vo.OrderVO;
import com.lony.orbiwise.vo.PageVO;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 * <p>实现订单的创建、查询、取消、支付等功能</p>
 *
 * @author lin504
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TravelOrderMapper travelOrderMapper;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private ScenicMapper scenicMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    /** 订单号日期格式 */
    private static final DateTimeFormatter ORDER_NO_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(Long userId, OrderCreateDTO orderCreateDTO) {
        // 查询门票信息
        Ticket ticket = ticketMapper.selectById(orderCreateDTO.getTicketId());
        if (ticket == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "门票不存在");
        }

        // 检查库存是否充足
        if (ticket.getStock() < orderCreateDTO.getQuantity()) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
        }

        // 扣减库存，增加已售数量
        ticketMapper.update(null, new LambdaUpdateWrapper<Ticket>()
                .eq(Ticket::getId, ticket.getId())
                .set(Ticket::getStock, ticket.getStock() - orderCreateDTO.getQuantity())
                .set(Ticket::getSoldCount, ticket.getSoldCount() + orderCreateDTO.getQuantity()));

        // 生成唯一订单号：时间戳 + 随机数
        String orderNo = generateOrderNo();

        // 计算订单总金额
        BigDecimal totalAmount = ticket.getPrice().multiply(BigDecimal.valueOf(orderCreateDTO.getQuantity()));

        // 创建订单记录
        TravelOrder order = new TravelOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTicketId(ticket.getId());
        order.setScenicId(ticket.getScenicId());
        order.setQuantity(orderCreateDTO.getQuantity());
        order.setTotalAmount(totalAmount);
        order.setContactName(orderCreateDTO.getContactName());
        order.setContactPhone(orderCreateDTO.getContactPhone());
        order.setVisitDate(orderCreateDTO.getVisitDate());
        order.setStatus(Constants.ORDER_STATUS_PENDING);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        travelOrderMapper.insert(order);

        // 记录用户下单行为（用于推荐算法）
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setTargetId(ticket.getScenicId());
        behavior.setTargetType("scenic");
        behavior.setBehaviorType("order");
        behavior.setBehaviorDesc("下单景点：" + ticket.getScenicId());
        behavior.setCreateTime(LocalDateTime.now());
        userBehaviorMapper.insert(behavior);

        return orderNo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageVO<OrderVO> getUserOrders(Long userId, Integer page, Integer size) {
        Page<TravelOrder> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<TravelOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TravelOrder::getUserId, userId)
                .orderByDesc(TravelOrder::getCreateTime);

        Page<TravelOrder> resultPage = travelOrderMapper.selectPage(pageParam, queryWrapper);

        List<OrderVO> voList = resultPage.getRecords().stream()
                .map(this::convertToOrderVO)
                .collect(Collectors.toList());

        return PageVO.of(voList, resultPage.getTotal(), page, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        TravelOrder order = travelOrderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        // 只有待支付状态才能取消
        if (order.getStatus() != Constants.ORDER_STATUS_PENDING) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "只有待支付订单可以取消");
        }

        // 恢复门票库存
        Ticket ticket = ticketMapper.selectById(order.getTicketId());
        if (ticket != null) {
            ticketMapper.update(null, new LambdaUpdateWrapper<Ticket>()
                    .eq(Ticket::getId, ticket.getId())
                    .set(Ticket::getStock, ticket.getStock() + order.getQuantity())
                    .set(Ticket::getSoldCount, ticket.getSoldCount() - order.getQuantity()));
        }

        // 更新订单状态为已取消
        order.setStatus(Constants.ORDER_STATUS_CANCELLED);
        order.setUpdateTime(LocalDateTime.now());
        travelOrderMapper.updateById(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long userId, Long orderId) {
        TravelOrder order = travelOrderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        // 只有待支付状态才能支付
        if (order.getStatus() != Constants.ORDER_STATUS_PENDING) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "订单状态不允许支付");
        }

        // 更新订单状态为已支付
        order.setStatus(Constants.ORDER_STATUS_PAID);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        travelOrderMapper.updateById(order);
    }

    /**
     * 生成唯一订单号
     * <p>格式：年月日时分秒 + 4位随机数</p>
     *
     * @return 订单编号
     */
    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(ORDER_NO_DATE_FORMAT);
        String randomStr = String.format("%04d", (int) (Math.random() * 10000));
        return "OW" + dateStr + randomStr;
    }

    /**
     * 将订单实体转换为订单 VO
     *
     * @param order 订单实体
     * @return 订单 VO
     */
    private OrderVO convertToOrderVO(TravelOrder order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setTicketId(order.getTicketId());
        vo.setScenicId(order.getScenicId());
        vo.setQuantity(order.getQuantity());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setContactName(order.getContactName());
        vo.setContactPhone(order.getContactPhone());
        vo.setVisitDate(order.getVisitDate());
        vo.setStatus(order.getStatus());
        vo.setPayTime(order.getPayTime());
        vo.setCreateTime(order.getCreateTime());

        // 查询景点名称
        Scenic scenic = scenicMapper.selectById(order.getScenicId());
        if (scenic != null) {
            vo.setScenicName(scenic.getName());
        }

        // 查询门票名称
        Ticket ticket = ticketMapper.selectById(order.getTicketId());
        if (ticket != null) {
            vo.setTicketName(ticket.getTicketName());
        }

        return vo;
    }

}
