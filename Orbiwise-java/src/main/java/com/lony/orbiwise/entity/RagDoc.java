package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RAG文档实体类
 * <p>对应数据库表 rag_doc，存储入库的文档信息</p>
 *
 * @author lin504
 */
@Data
@TableName("rag_doc")
public class RagDoc {

    /** 文档ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文档标题 */
    private String title;

    /** 文档内容 */
    private String content;

    /** 文档来源 */
    private String source;

    /** 文档类型，如"travel_guide"、"scenic_intro" */
    private String docType;

    /** 分块数量 */
    private Integer chunkCount;

    /** 文档状态：0-处理中，1-已完成，2-失败 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
