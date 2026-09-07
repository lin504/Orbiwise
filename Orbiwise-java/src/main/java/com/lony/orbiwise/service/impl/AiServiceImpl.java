package com.lony.orbiwise.service.impl;

import com.lony.orbiwise.dto.RagChatDTO;
import com.lony.orbiwise.dto.RagStoreDTO;
import com.lony.orbiwise.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 服务实现类
 * <p>通过 RestTemplate 调用 Python FastAPI 的 RAG 服务接口，
 * 实现文档入库、AI 问答、会话管理等功能</p>
 *
 * @author lin504
 */
@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private RestTemplate restTemplate;

    /** Python AI 服务的基础 URL */
    @Value("${ai.service-url}")
    private String aiServiceUrl;

    /** RAG 文档入库接口路径 */
    private static final String RAG_STORE_PATH = "/api/rag/store";

    /** RAG 问答接口路径 */
    private static final String RAG_CHAT_PATH = "/api/rag/chat";

    /** RAG 清空会话接口路径 */
    private static final String RAG_CLEAR_PATH = "/api/rag/clear_session";

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> storeDocument(RagStoreDTO ragStoreDTO) {
        String url = aiServiceUrl + RAG_STORE_PATH;

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", ragStoreDTO.getTitle());
        requestBody.put("content", ragStoreDTO.getContent());
        requestBody.put("source", ragStoreDTO.getSource());
        requestBody.put("doc_type", ragStoreDTO.getDocType());

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 调用 Python 服务
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        return response.getBody();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> chat(RagChatDTO ragChatDTO, Long userId) {
        String url = aiServiceUrl + RAG_CHAT_PATH;

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("question", ragChatDTO.getQuestion());
        requestBody.put("session_id", ragChatDTO.getSessionId());
        requestBody.put("user_id", userId);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 调用 Python 服务
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        return response.getBody();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSession(String sessionId) {
        String url = aiServiceUrl + RAG_CLEAR_PATH;

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("session_id", sessionId);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 调用 Python 服务
        restTemplate.postForEntity(url, entity, Map.class);
    }

}
