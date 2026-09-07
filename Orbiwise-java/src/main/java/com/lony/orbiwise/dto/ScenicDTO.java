package com.lony.orbiwise.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 景点新增/编辑请求DTO
 * <p>接收管理端创建或编辑景点时的参数</p>
 *
 * @author lin504
 */
@Data
public class ScenicDTO {

    /** 景点名称 */
    @NotBlank(message = "景点名称不能为空")
    private String name;

    /** 景点描述 */
    private String description;

    /** 封面图片URL */
    private String coverImage;

    /** 图片列表，JSON数组 */
    private String images;

    /** 景点地址 */
    private String address;

    /** 经度 */
    private BigDecimal longitude;

    /** 纬度 */
    private BigDecimal latitude;

    /** 景点分类 */
    private String category;

    /** 景点级别 */
    private String level;

    /** 开放时间描述 */
    private String openTime;

    /** 景点状态：0-下架，1-上架 */
    private Integer status;

}
