package com.lony.orbiwise.vo;

import lombok.Data;

/**
 * 推荐结果VO
 * <p>推荐算法返回的单个推荐项</p>
 *
 * @author lin504
 */
@Data
public class RecommendVO {

    /** 景点ID */
    private Long scenicId;

    /** 景点名称 */
    private String name;

    /** 推荐分数 */
    private Double score;

    /** 推荐理由 */
    private String reason;

}
