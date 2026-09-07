package com.lony.orbiwise.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RAG问答请求DTO
 * <p>接收用户AI问答时的参数</p>
 *
 * @author lin504
 */
@Data
public class RagChatDTO {

    /** 用户提问内容 */
    @NotBlank(message = "问题不能为空")
    private String question;

    /** 会话ID，为空时创建新会话 */
    private String sessionId;

}
