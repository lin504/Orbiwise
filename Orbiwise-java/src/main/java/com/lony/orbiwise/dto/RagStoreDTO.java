package com.lony.orbiwise.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RAG文档入库请求DTO
 * <p>接收文档入库时的参数，调用Python AI服务进行向量化存储</p>
 *
 * @author lin504
 */
@Data
public class RagStoreDTO {

    /** 文档标题 */
    @NotBlank(message = "文档标题不能为空")
    private String title;

    /** 文档内容 */
    @NotBlank(message = "文档内容不能为空")
    private String content;

    /** 文档来源 */
    private String source;

    /** 文档类型 */
    private String docType;

}
