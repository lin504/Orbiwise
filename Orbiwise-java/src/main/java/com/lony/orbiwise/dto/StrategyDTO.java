package com.lony.orbiwise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 攻略发布请求DTO
 * <p>接收用户发布旅游攻略时的参数</p>
 *
 * @author lin504
 */
@Data
public class StrategyDTO {

    /** 关联景点ID */
    @NotNull(message = "景点ID不能为空")
    private Long scenicId;

    /** 攻略标题 */
    @NotBlank(message = "攻略标题不能为空")
    private String title;

    /** 攻略内容 */
    @NotBlank(message = "攻略内容不能为空")
    private String content;

    /** 封面图片URL */
    private String coverImage;

    /** 标签，逗号分隔 */
    private String tags;

}
