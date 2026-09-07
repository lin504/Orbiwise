package com.lony.orbiwise.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 景点详情VO
 * <p>景点详情信息，包含关联的门票列表</p>
 *
 * @author lin504
 */
@Data
public class ScenicVO {

    /** 景点ID */
    private Long id;

    /** 景点名称 */
    private String name;

    /** 景点描述 */
    private String description;

    /** 封面图片URL */
    private String coverImage;

    /** 图片列表 */
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

    /** 开放时间 */
    private String openTime;

    /** 景点状态 */
    private Integer status;

    /** 浏览次数 */
    private Integer viewCount;

    /** 平均评分 */
    private BigDecimal avgRating;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 关联的门票列表 */
    private List<TicketVO> tickets;

    /**
     * 门票简要VO（内部类）
     */
    @Data
    public static class TicketVO {
        /** 门票ID */
        private Long id;
        /** 门票名称 */
        private String ticketName;
        /** 售价 */
        private BigDecimal price;
        /** 原价 */
        private BigDecimal originalPrice;
        /** 门票类型 */
        private Integer ticketType;
    }

}
