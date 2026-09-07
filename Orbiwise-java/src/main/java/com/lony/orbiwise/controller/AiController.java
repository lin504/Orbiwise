package com.lony.orbiwise.controller;

import com.lony.orbiwise.dto.RagChatDTO;
import com.lony.orbiwise.dto.RagStoreDTO;
import com.lony.orbiwise.service.AiService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI 服务控制器
 * <p>提供 RAG 文档入库、AI 问答、会话管理接口</p>
 *
 * @author lin504
 */
@Tag(name = "AI服务", description = "RAG文档入库、AI问答接口")
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /**
     * 文档入库
     *
     * @param ragStoreDTO 文档入库参数
     * @return 入库结果
     */
    @Operation(summary = "文档入库", description = "将文档入库到RAG系统，进行向量化存储")
    @PostMapping("/rag/store")
    public Result<Map<String, Object>> storeDocument(@Valid @RequestBody RagStoreDTO ragStoreDTO) {
        Map<String, Object> result = aiService.storeDocument(ragStoreDTO);
        return Result.success(result);
    }

    /**
     * AI 问答
     *
     * @param request    HTTP 请求
     * @param ragChatDTO 问答参数
     * @return 问答结果
     */
    @Operation(summary = "AI问答", description = "基于RAG的AI智能问答")
    @PostMapping("/rag/chat")
    public Result<Map<String, Object>> chat(HttpServletRequest request,
                                             @Valid @RequestBody RagChatDTO ragChatDTO) {
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        Map<String, Object> result = aiService.chat(ragChatDTO, userId);
        return Result.success(result);
    }

    /**
     * 清空会话
     *
     * @param body 请求体（含 sessionId 字段）
     * @return 操作结果
     */
    @Operation(summary = "清空会话", description = "清空AI对话会话记录")
    @PostMapping("/rag/clear")
    public Result<Void> clearSession(@RequestBody Map<String, String> body) {
        String sessionId = body.get("sessionId");
        aiService.clearSession(sessionId);
        return Result.success();
    }

}
