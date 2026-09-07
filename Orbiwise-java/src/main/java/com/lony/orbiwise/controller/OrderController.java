package com.lony.orbiwise.controller;

import com.lony.orbiwise.dto.OrderCreateDTO;
import com.lony.orbiwise.service.OrderService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.Result;
import com.lony.orbiwise.vo.OrderVO;
import com.lony.orbiwise.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * <p>提供订单的创建、查询、取消、支付接口</p>
 *
 * @author lin504
 */
@Tag(name = "订单管理", description = "订单创建、查询、取消、支付接口")
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @param request        HTTP 请求（获取当前用户ID）
     * @param orderCreateDTO 下单参数
     * @return 订单编号
     */
    @Operation(summary = "创建订单", description = "用户下单购票，自动扣减库存")
    @PostMapping
    public Result<String> create(HttpServletRequest request,
                                  @Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        String orderNo = orderService.createOrder(userId, orderCreateDTO);
        return Result.success(orderNo);
    }

    /**
     * 我的订单列表
     *
     * @param request HTTP 请求
     * @param page    页码
     * @param size    每页大小
     * @return 分页订单列表
     */
    @Operation(summary = "我的订单", description = "查询当前用户的订单列表")
    @GetMapping("/list")
    public Result<PageVO<OrderVO>> myOrders(
            HttpServletRequest request,
            @RequestParam(defaultValue = "" + Constants.DEFAULT_PAGE_NUM) Integer page,
            @RequestParam(defaultValue = "" + Constants.DEFAULT_PAGE_SIZE) Integer size) {
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        PageVO<OrderVO> result = orderService.getUserOrders(userId, page, size);
        return Result.success(result);
    }

    /**
     * 取消订单
     *
     * @param request HTTP 请求
     * @param id      订单ID
     * @return 操作结果
     */
    @Operation(summary = "取消订单", description = "取消待支付订单，恢复门票库存")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        orderService.cancelOrder(userId, id);
        return Result.success();
    }

    /**
     * 支付订单
     *
     * @param request HTTP 请求
     * @param id      订单ID
     * @return 操作结果
     */
    @Operation(summary = "支付订单", description = "模拟支付订单")
    @PutMapping("/{id}/pay")
    public Result<Void> pay(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        orderService.payOrder(userId, id);
        return Result.success();
    }

}
