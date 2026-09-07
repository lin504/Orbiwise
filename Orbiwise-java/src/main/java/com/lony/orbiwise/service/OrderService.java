package com.lony.orbiwise.service;

import com.lony.orbiwise.dto.OrderCreateDTO;
import com.lony.orbiwise.vo.OrderVO;
import com.lony.orbiwise.vo.PageVO;

/**
 * 订单服务接口
 * <p>提供订单的创建、查询、取消、支付等功能</p>
 *
 * @author lin504
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param userId       用户ID
     * @param orderCreateDTO 下单参数
     * @return 订单编号
     */
    String createOrder(Long userId, OrderCreateDTO orderCreateDTO);

    /**
     * 查询用户订单列表
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 分页订单列表
     */
    PageVO<OrderVO> getUserOrders(Long userId, Integer page, Integer size);

    /**
     * 取消订单（恢复库存）
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 模拟支付
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     */
    void payOrder(Long userId, Long orderId);

}
