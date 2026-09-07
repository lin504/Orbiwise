package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体类
 * <p>对应数据库表 comment，存储景点评论及回复</p>
 *
 * @author lin504
 */
@Data
@TableName("comment")
public class Comment {

    /** 评论ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评论用户ID */
    private Long userId;

    /** 关联景点ID */
    private Long scenicId;

    /** 关联订单ID（可选） */
    private Long orderId;

    /** 评论内容 */
    private String content;

    /** 评分（1-5） */
    private Integer rating;

    /** 图片，JSON数组格式存储 */
    private String images;

    /** 父评论ID，用于回复嵌套，顶级评论为0 */
    private Long parentId;

    /** 回复目标用户ID */
    private Long replyToUserId;

    /** 评论状态：0-待审核，1-已通过，2-已拒绝 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
