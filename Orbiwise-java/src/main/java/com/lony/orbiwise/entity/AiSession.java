package com.lony.orbiwise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI会话实体类
 * <p>对应数据库表 ai_session，存储用户与AI的对话会话信息</p>
 *
 * @author lin504
 */
@Data
@TableName("ai_session")
public class AiSession {

    /** 会话ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 会话唯一标识（UUID） */
    private String sessionId;

    /** 用户ID */
    private Long userId;

    /** 会话标题 */
    private String title;

    /** 消息数量 */
    private Integer messageCount;

    /** 会话状态：0-已结束，1-进行中 */
    private Integer status;

    /** 最后活跃时间 */
    private LocalDateTime lastActiveTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
