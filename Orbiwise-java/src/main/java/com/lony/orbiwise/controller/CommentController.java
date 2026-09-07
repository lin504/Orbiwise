package com.lony.orbiwise.controller;

import com.lony.orbiwise.dto.CommentDTO;
import com.lony.orbiwise.service.CommentService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.Result;
import com.lony.orbiwise.vo.CommentVO;
import com.lony.orbiwise.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 评论控制器
 * <p>提供评论的查询、发布、回复接口</p>
 *
 * @author lin504
 */
@Tag(name = "评论管理", description = "评论发布、查询、回复接口")
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 景点评论列表
     *
     * @param scenicId 景点ID
     * @param page     页码
     * @param size     每页大小
     * @return 分页评论列表
     */
    @Operation(summary = "景点评论", description = "查询景点的评论列表，包含子评论")
    @GetMapping("/scenic/{scenicId}")
    public Result<PageVO<CommentVO>> listByScenicId(
            @PathVariable Long scenicId,
            @RequestParam(defaultValue = "" + Constants.DEFAULT_PAGE_NUM) Integer page,
            @RequestParam(defaultValue = "" + Constants.DEFAULT_PAGE_SIZE) Integer size) {
        PageVO<CommentVO> result = commentService.listByScenicId(scenicId, page, size);
        return Result.success(result);
    }

    /**
     * 发布评论（内部敏感词校验）
     *
     * @param request    HTTP 请求
     * @param commentDTO 评论参数
     * @return 评论ID
     */
    @Operation(summary = "发布评论", description = "发布景点评论，自动进行敏感词校验")
    @PostMapping
    public Result<Long> publish(HttpServletRequest request,
                                @Valid @RequestBody CommentDTO commentDTO) {
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        Long commentId = commentService.publishComment(userId, commentDTO);
        return Result.success(commentId);
    }

    /**
     * 回复评论
     *
     * @param request HTTP 请求
     * @param id      父评论ID
     * @param body    请求体（含 content 字段）
     * @return 回复评论ID
     */
    @Operation(summary = "回复评论", description = "回复已有评论")
    @PostMapping("/{id}/reply")
    public Result<Long> reply(HttpServletRequest request,
                              @PathVariable Long id,
                              @RequestBody Map<String, String> body) {
        Long userId = (Long) request.getAttribute(Constants.REQUEST_ATTR_USER_ID);
        String content = body.get("content");
        Long commentId = commentService.replyComment(userId, id, content);
        return Result.success(commentId);
    }

}
