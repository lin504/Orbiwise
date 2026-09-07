package com.lony.orbiwise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 门票请求DTO
 * <p>接收管理端创建或编辑门票时的参数</p>
 *
 * @author lin504
 */
@Data
public class TicketDTO {

    /** 所属景点ID */
    @NotNull(message = "景点ID不能为空")
    private Long scenicId;

    /** 门票名称 */
    @NotBlank(message = "门票名称不能为空")
    private String ticketName;

    /** 售价 */
    @NotNull(message = "售价不能为空")
    private BigDecimal price;

    /** 原价 */
    private BigDecimal originalPrice;

    /** 库存数量 */
    @NotNull(message = "库存不能为空")
    private Integer stock;

    /** 门票类型：1-普通票，2-优惠票，3-套票 */
    private Integer ticketType;

    /** 有效期开始时间 */
    private LocalDateTime validStart;

    /** 有效期结束时间 */
    private LocalDateTime validEnd;

    /** 门票状态：0-下架，1-上架 */
    private Integer status;

}
