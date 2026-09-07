package com.lony.orbiwise.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 攻略详情VO
 * <p>攻略详情信息，包含作者信息</p>
 *
 * @author lin504
 */
@Data
public class StrategyVO {

    /** 攻略ID */
    private Long id;

    /** 作者用户ID */
    private Long userId;

    /** 作者昵称 */
    private String authorNickname;

    /** 作者头像 */
    private String authorAvatar;

    /** 关联景点ID */
    private Long scenicId;

    /** 关联景点名称 */
    private String scenicName;

    /** 攻略标题 */
    private String title;

    /** 攻略内容 */
    private String content;

    /** 封面图片URL */
    private String coverImage;

    /** 标签 */
    private String tags;

    /** 浏览次数 */
    private Integer viewCount;

    /** 点赞次数 */
    private Integer likeCount;

    /** 收藏次数 */
    private Integer collectCount;

    /** 攻略状态 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

}
