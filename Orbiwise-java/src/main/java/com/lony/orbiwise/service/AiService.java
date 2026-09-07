package com.lony.orbiwise.service;

import com.lony.orbiwise.dto.RagChatDTO;
import com.lony.orbiwise.dto.RagStoreDTO;

import java.util.Map;

/**
 * AI 服务接口
 * <p>提供 RAG 文档入库、AI 问答、会话管理等功能，通过 RestTemplate 调用 Python FastAPI 服务</p>
 *
 * @author lin504
 */
public interface AiService {

    /**
     * 文档入库
     *
     * @param ragStoreDTO 文档入库参数
     * @return 入库结果
     */
    Map<String, Object> storeDocument(RagStoreDTO ragStoreDTO);

    /**
     * AI 问答
     *
     * @param ragChatDTO 问答参数
     * @param userId     用户ID
     * @return 问答结果
     */
    Map<String, Object> chat(RagChatDTO ragChatDTO, Long userId);

    /**
     * 清空会话
     *
     * @param sessionId 会话ID
     */
    void clearSession(String sessionId);

}
