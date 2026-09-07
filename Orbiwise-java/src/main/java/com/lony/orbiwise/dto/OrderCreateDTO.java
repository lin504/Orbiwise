package com.lony.orbiwise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 订单创建请求DTO
 * <p>接收用户下单时的参数</p>
 *
 * @author lin504
 */
@Data
public class OrderCreateDTO {

    /** 门票ID */
    @NotNull(message = "门票ID不能为空")
    private Long ticketId;

    /** 购买数量 */
    @NotNull(message = "购买数量不能为空")
    private Integer quantity;

    /** 联系人姓名 */
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    /** 联系人手机号 */
    @NotBlank(message = "联系人手机号不能为空")
    private String contactPhone;

    /** 游览日期 */
    @NotNull(message = "游览日期不能为空")
    private LocalDate visitDate;

}
