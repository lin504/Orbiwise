package com.lony.orbiwise.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单详情VO
 * <p>订单详情信息，包含景点和门票名称</p>
 *
 * @author lin504
 */
@Data
public class OrderVO {

    /** 订单ID */
    private Long id;

    /** 订单编号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 门票ID */
    private Long ticketId;

    /** 景点ID */
    private Long scenicId;

    /** 景点名称 */
    private String scenicName;

    /** 门票名称 */
    private String ticketName;

    /** 购买数量 */
    private Integer quantity;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 联系人姓名 */
    private String contactName;

    /** 联系人手机号 */
    private String contactPhone;

    /** 游览日期 */
    private LocalDate visitDate;

    /** 订单状态：0-待支付，1-已支付，2-已取消，3-已退款 */
    private Integer status;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 创建时间 */
    private LocalDateTime createTime;

}
