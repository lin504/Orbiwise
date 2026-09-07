package com.lony.orbiwise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lony.orbiwise.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论 Mapper 接口
 * <p>提供评论表的基础 CRUD 操作，支持按景点查询评论（含子评论）</p>
 *
 * @author lin504
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}
