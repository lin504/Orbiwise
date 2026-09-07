package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 旅游订单实体类
 * <p>对应数据库表 travel_order，存储用户购票订单信息</p>
 *
 * @author lin504
 */
@Data
@TableName("travel_order")
public class TravelOrder {

    /** 订单ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单编号，唯一业务单号 */
    private String orderNo;

    /** 下单用户ID */
    private Long userId;

    /** 门票ID */
    private Long ticketId;

    /** 景点ID */
    private Long scenicId;

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
