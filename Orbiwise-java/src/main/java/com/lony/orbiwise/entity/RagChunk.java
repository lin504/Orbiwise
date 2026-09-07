package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RAG文档分块实体类
 * <p>对应数据库表 rag_chunk，存储文档分块后的内容片段</p>
 *
 * @author lin504
 */
@Data
@TableName("rag_chunk")
public class RagChunk {

    /** 分块ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属文档ID */
    private Long docId;

    /** 分块索引，在文档中的顺序 */
    private Integer chunkIndex;

    /** 分块内容 */
    private String content;

    /** 向量ID，对应向量数据库中的向量 */
    private String vectorId;

    /** Token数量 */
    private Integer tokenCount;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
