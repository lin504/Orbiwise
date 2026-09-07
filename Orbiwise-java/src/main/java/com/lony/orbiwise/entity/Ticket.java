package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 门票实体类
 * <p>对应数据库表 ticket，存储景点门票信息</p>
 *
 * @author lin504
 */
@Data
@TableName("ticket")
public class Ticket {

    /** 门票ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属景点ID */
    private Long scenicId;

    /** 门票名称，如"成人票"、"学生票" */
    private String ticketName;

    /** 售价 */
    private BigDecimal price;

    /** 原价 */
    private BigDecimal originalPrice;

    /** 库存数量 */
    private Integer stock;

    /** 已售数量 */
    private Integer soldCount;

    /** 门票类型：1-普通票，2-优惠票，3-套票 */
    private Integer ticketType;

    /** 有效期开始时间 */
    private LocalDateTime validStart;

    /** 有效期结束时间 */
    private LocalDateTime validEnd;

    /** 门票状态：0-下架，1-上架 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
