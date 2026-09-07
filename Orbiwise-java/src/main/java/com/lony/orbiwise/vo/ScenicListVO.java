package com.lony.orbiwise.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 景点列表简要VO
 * <p>景点列表页展示用的简要信息</p>
 *
 * @author lin504
 */
@Data
public class ScenicListVO {

    /** 景点ID */
    private Long id;

    /** 景点名称 */
    private String name;

    /** 封面图片URL */
    private String coverImage;

    /** 景点地址 */
    private String address;

    /** 景点分类 */
    private String category;

    /** 景点级别 */
    private String level;

    /** 平均评分 */
    private BigDecimal avgRating;

    /** 浏览次数 */
    private Integer viewCount;

}
