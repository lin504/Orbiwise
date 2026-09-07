package com.lony.orbiwise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论请求DTO
 * <p>接收用户发布评论时的参数</p>
 *
 * @author lin504
 */
@Data
public class CommentDTO {

    /** 关联景点ID */
    @NotNull(message = "景点ID不能为空")
    private Long scenicId;

    /** 关联订单ID（可选） */
    private Long orderId;

    /** 评论内容 */
    @NotBlank(message = "评论内容不能为空")
    private String content;

    /** 评分（1-5） */
    private Integer rating;

    /** 图片，JSON数组 */
    private String images;

}
