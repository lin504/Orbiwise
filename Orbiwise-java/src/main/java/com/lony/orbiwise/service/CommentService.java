package com.lony.orbiwise.service;

import com.lony.orbiwise.dto.CommentDTO;
import com.lony.orbiwise.vo.CommentVO;
import com.lony.orbiwise.vo.PageVO;

/**
 * 评论服务接口
 * <p>提供评论的查询、发布、回复等功能</p>
 *
 * @author lin504
 */
public interface CommentService {

    /**
     * 查询景点评论列表
     *
     * @param scenicId 景点ID
     * @param page     页码
     * @param size     每页大小
     * @return 分页评论列表
     */
    PageVO<CommentVO> listByScenicId(Long scenicId, Integer page, Integer size);

    /**
     * 发布评论（内部调用敏感词校验）
     *
     * @param userId    用户ID
     * @param commentDTO 评论参数
     * @return 评论ID
     */
    Long publishComment(Long userId, CommentDTO commentDTO);

    /**
     * 回复评论
     *
     * @param userId   用户ID
     * @param parentId 父评论ID
     * @param content  回复内容
     * @return 评论ID
     */
    Long replyComment(Long userId, Long parentId, String content);

}
