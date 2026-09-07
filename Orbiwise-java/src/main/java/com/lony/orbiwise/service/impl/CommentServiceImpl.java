package com.lony.orbiwise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lony.orbiwise.dto.CommentDTO;
import com.lony.orbiwise.entity.Comment;
import com.lony.orbiwise.entity.User;
import com.lony.orbiwise.exception.BusinessException;
import com.lony.orbiwise.mapper.CommentMapper;
import com.lony.orbiwise.mapper.UserMapper;
import com.lony.orbiwise.service.CommentService;
import com.lony.orbiwise.service.SensitiveWordService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.util.ResultCode;
import com.lony.orbiwise.vo.CommentVO;
import com.lony.orbiwise.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 * <p>实现评论的查询、发布、回复等功能，发布评论时进行敏感词校验</p>
 *
 * @author lin504
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageVO<CommentVO> listByScenicId(Long scenicId, Integer page, Integer size) {
        Page<Comment> pageParam = new Page<>(page, size);

        // 查询顶级评论（parentId 为 0）
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getScenicId, scenicId)
                .eq(Comment::getParentId, 0L)
                .eq(Comment::getStatus, Constants.COMMENT_STATUS_APPROVED)
                .orderByDesc(Comment::getCreateTime);

        Page<Comment> resultPage = commentMapper.selectPage(pageParam, queryWrapper);

        // 查询该景点所有评论（含子评论）
        LambdaQueryWrapper<Comment> allQuery = new LambdaQueryWrapper<>();
        allQuery.eq(Comment::getScenicId, scenicId)
                .eq(Comment::getStatus, Constants.COMMENT_STATUS_APPROVED);
        List<Comment> allComments = commentMapper.selectList(allQuery);

        // 将所有评论转换为 VO
        List<CommentVO> allVOs = allComments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 按 parentId 分组，构建子评论映射
        Map<Long, List<CommentVO>> childrenMap = allVOs.stream()
                .filter(vo -> vo.getParentId() != null && vo.getParentId() > 0)
                .collect(Collectors.groupingBy(CommentVO::getParentId));

        // 为顶级评论设置子评论列表
        List<CommentVO> topVOs = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        topVOs.forEach(vo -> {
            List<CommentVO> children = childrenMap.getOrDefault(vo.getId(), new ArrayList<>());
            vo.setChildren(children);
        });

        return PageVO.of(topVOs, resultPage.getTotal(), page, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long publishComment(Long userId, CommentDTO commentDTO) {
        // 敏感词校验
        String sensitiveWord = sensitiveWordService.checkText(commentDTO.getContent());
        if (sensitiveWord != null) {
            throw new BusinessException(ResultCode.CONTAINS_SENSITIVE_WORD,
                    "内容包含敏感词: " + sensitiveWord);
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setScenicId(commentDTO.getScenicId());
        comment.setOrderId(commentDTO.getOrderId());
        comment.setContent(commentDTO.getContent());
        comment.setRating(commentDTO.getRating());
        comment.setImages(commentDTO.getImages());
        comment.setParentId(0L);
        comment.setReplyToUserId(0L);
        comment.setStatus(Constants.COMMENT_STATUS_APPROVED);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());

        commentMapper.insert(comment);
        return comment.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long replyComment(Long userId, Long parentId, String content) {
        // 敏感词校验
        String sensitiveWord = sensitiveWordService.checkText(content);
        if (sensitiveWord != null) {
            throw new BusinessException(ResultCode.CONTAINS_SENSITIVE_WORD,
                    "内容包含敏感词: " + sensitiveWord);
        }

        // 查询父评论
        Comment parentComment = commentMapper.selectById(parentId);
        if (parentComment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "被回复的评论不存在");
        }

        Comment reply = new Comment();
        reply.setUserId(userId);
        reply.setScenicId(parentComment.getScenicId());
        reply.setOrderId(parentComment.getOrderId());
        reply.setContent(content);
        reply.setParentId(parentId);
        reply.setReplyToUserId(parentComment.getUserId());
        reply.setStatus(Constants.COMMENT_STATUS_APPROVED);
        reply.setCreateTime(LocalDateTime.now());
        reply.setUpdateTime(LocalDateTime.now());

        commentMapper.insert(reply);
        return reply.getId();
    }

    /**
     * 将评论实体转换为 VO
     *
     * @param comment 评论实体
     * @return 评论 VO
     */
    private CommentVO convertToVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setUserId(comment.getUserId());
        vo.setScenicId(comment.getScenicId());
        vo.setContent(comment.getContent());
        vo.setRating(comment.getRating());
        vo.setImages(comment.getImages());
        vo.setParentId(comment.getParentId());
        vo.setReplyToUserId(comment.getReplyToUserId());
        vo.setStatus(comment.getStatus());
        vo.setCreateTime(comment.getCreateTime());

        // 查询评论用户信息
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }

        // 查询回复目标用户昵称
        if (comment.getReplyToUserId() != null && comment.getReplyToUserId() > 0) {
            User replyToUser = userMapper.selectById(comment.getReplyToUserId());
            if (replyToUser != null) {
                vo.setReplyToNickname(replyToUser.getNickname());
            }
        }

        return vo;
    }

}
