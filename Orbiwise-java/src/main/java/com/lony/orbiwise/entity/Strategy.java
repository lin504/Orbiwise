package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅游攻略实体类
 * <p>对应数据库表 strategy，存储用户发布的旅游攻略</p>
 *
 * @author lin504
 */
@Data
@TableName("strategy")
public class Strategy {

    /** 攻略ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 作者用户ID */
    private Long userId;

    /** 关联景点ID */
    private Long scenicId;

    /** 攻略标题 */
    private String title;

    /** 攻略内容 */
    private String content;

    /** 封面图片URL */
    private String coverImage;

    /** 标签，逗号分隔 */
    private String tags;

    /** 浏览次数 */
    private Integer viewCount;

    /** 点赞次数 */
    private Integer likeCount;

    /** 收藏次数 */
    private Integer collectCount;

    /** 攻略状态：0-草稿，1-已发布，2-已下架 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
