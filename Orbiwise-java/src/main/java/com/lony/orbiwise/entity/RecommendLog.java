package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐日志实体类
 * <p>对应数据库表 recommend_log，记录推荐算法的执行日志</p>
 *
 * @author lin504
 */
@Data
@TableName("recommend_log")
public class RecommendLog {

    /** 日志ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 推荐的景点ID列表，逗号分隔 */
    private String scenicIds;

    /** 推荐算法名称，如"collaborative_filtering" */
    private String algorithm;

    /** 请求参数，JSON格式 */
    private String requestParams;

    /** 推荐结果数量 */
    private Integer resultCount;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
