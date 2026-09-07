package com.lony.orbiwise.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论详情VO
 * <p>评论详情信息，包含用户信息和子评论列表</p>
 *
 * @author lin504
 */
@Data
public class CommentVO {

    /** 评论ID */
    private Long id;

    /** 评论用户ID */
    private Long userId;

    /** 评论用户昵称 */
    private String userNickname;

    /** 评论用户头像 */
    private String userAvatar;

    /** 关联景点ID */
    private Long scenicId;

    /** 评论内容 */
    private String content;

    /** 评分 */
    private Integer rating;

    /** 图片 */
    private String images;

    /** 父评论ID */
    private Long parentId;

    /** 回复目标用户ID */
    private Long replyToUserId;

    /** 回复目标用户昵称 */
    private String replyToNickname;

    /** 评论状态 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 子评论列表 */
    private List<CommentVO> children;

}
