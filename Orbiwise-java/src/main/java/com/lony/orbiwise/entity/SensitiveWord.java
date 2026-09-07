package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 敏感词实体类
 * <p>对应数据库表 sensitive_word，存储敏感词库</p>
 *
 * @author lin504
 */
@Data
@TableName("sensitive_word")
public class SensitiveWord {

    /** 敏感词ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 敏感词内容 */
    private String word;

    /** 分类，如"政治"、"色情"、"广告" */
    private String category;

    /** 敏感级别：1-低，2-中，3-高 */
    private Integer level;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
