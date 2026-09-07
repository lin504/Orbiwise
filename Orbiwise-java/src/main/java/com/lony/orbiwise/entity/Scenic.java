package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点实体类
 * <p>对应数据库表 scenic，存储旅游景点的详细信息</p>
 *
 * @author lin504
 */
@Data
@TableName("scenic")
public class Scenic {

    /** 景点ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 景点名称 */
    private String name;

    /** 景点描述 */
    private String description;

    /** 封面图片URL */
    private String coverImage;

    /** 图片列表，JSON数组格式存储 */
    private String images;

    /** 景点地址 */
    private String address;

    /** 经度 */
    private BigDecimal longitude;

    /** 纬度 */
    private BigDecimal latitude;

    /** 景点分类，如"自然风光"、"历史古迹" */
    private String category;

    /** 景点级别，如"AAAAA"、"AAAA" */
    private String level;

    /** 开放时间描述 */
    private String openTime;

    /** 景点状态：0-下架，1-上架 */
    private Integer status;

    /** 浏览次数 */
    private Integer viewCount;

    /** 平均评分 */
    private BigDecimal avgRating;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
