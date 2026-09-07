package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户行为实体类
 * <p>对应数据库表 user_behavior，记录用户浏览、收藏、下单等行为数据，用于推荐算法</p>
 *
 * @author lin504
 */
@Data
@TableName("user_behavior")
public class UserBehavior {

    /** 行为ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 目标ID（景点ID、攻略ID等） */
    private Long targetId;

    /** 目标类型：scenic-景点，strategy-攻略 */
    private String targetType;

    /** 行为类型：view-浏览，collect-收藏，order-下单，like-点赞 */
    private String behaviorType;

    /** 行为描述 */
    private String behaviorDesc;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
