-- ============================================================
-- Orbiwise 在线旅游平台 - 数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- 说明: 共16张核心业务表，包含索引、字段注释
-- ============================================================

-- 创建数据库（MySQL 不支持 CREATE DATABASE COMMENT，注释写在上方）
CREATE DATABASE IF NOT EXISTS `orbiwise` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `orbiwise`;

-- ============================================================
-- 1. 用户表 (user)
-- ============================================================
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键自增',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名，唯一',
    `password` VARCHAR(255) NOT NULL COMMENT '密码，BCrypt加密存储',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `status` TINYINT DEFAULT 1 COMMENT '账号状态：0-禁用，1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 角色表 (role)
-- ============================================================
CREATE TABLE `role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID，主键自增',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称，如admin、user',
    `role_desc` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================================
-- 3. 权限表 (permission)
-- ============================================================
CREATE TABLE `permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID，主键自增',
    `perm_name` VARCHAR(100) NOT NULL COMMENT '权限名称，如scenic:read',
    `perm_code` VARCHAR(100) NOT NULL COMMENT '权限编码，用于代码中校验',
    `perm_desc` VARCHAR(200) DEFAULT NULL COMMENT '权限描述',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '所属业务模块标识',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_perm_code` (`perm_code`),
    KEY `idx_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ============================================================
-- 4. 用户-角色关联表 (user_role)
-- ============================================================
CREATE TABLE `user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键自增',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- ============================================================
-- 5. 角色-权限关联表 (role_perm)
-- ============================================================
CREATE TABLE `role_perm` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键自增',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `perm_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_perm` (`role_id`, `perm_id`),
    KEY `idx_perm_id` (`perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

-- ============================================================
-- 6. 景点表 (scenic)
-- ============================================================
CREATE TABLE `scenic` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '景点ID，主键自增',
    `name` VARCHAR(200) NOT NULL COMMENT '景点名称',
    `description` TEXT COMMENT '景点详细描述',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `images` VARCHAR(2000) DEFAULT NULL COMMENT '景点图片列表，JSON数组格式',
    `address` VARCHAR(500) DEFAULT NULL COMMENT '景点地址',
    `longitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '经度',
    `latitude` DECIMAL(10, 6) DEFAULT NULL COMMENT '纬度',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '景点分类：自然风光/历史古迹/主题乐园等',
    `level` VARCHAR(20) DEFAULT NULL COMMENT '景区等级：A/AA/AAA/AAAA/AAAAA',
    `open_time` VARCHAR(100) DEFAULT NULL COMMENT '开放时间描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `view_count` BIGINT DEFAULT 0 COMMENT '浏览次数',
    `avg_rating` DECIMAL(3, 2) DEFAULT 0.00 COMMENT '平均评分，1.00-5.00',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_avg_rating` (`avg_rating`),
    FULLTEXT KEY `ft_name_desc` (`name`, `description`) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点表';

-- ============================================================
-- 7. 门票表 (ticket)
-- ============================================================
CREATE TABLE `ticket` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '门票ID，主键自增',
    `scenic_id` BIGINT NOT NULL COMMENT '关联景点ID',
    `ticket_name` VARCHAR(200) NOT NULL COMMENT '门票名称，如成人票、学生票',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '门票价格',
    `original_price` DECIMAL(10, 2) DEFAULT NULL COMMENT '原价',
    `stock` INT DEFAULT 0 COMMENT '库存数量',
    `sold_count` INT DEFAULT 0 COMMENT '已售数量',
    `ticket_type` TINYINT DEFAULT 1 COMMENT '票种类型：1-成人票，2-学生票，3-儿童票，4-老年票',
    `valid_start` DATE DEFAULT NULL COMMENT '有效期开始日期',
    `valid_end` DATE DEFAULT NULL COMMENT '有效期结束日期',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-停售，1-在售',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_scenic_id` (`scenic_id`),
    KEY `idx_status` (`status`),
    KEY `idx_valid` (`valid_start`, `valid_end`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门票表';

-- ============================================================
-- 8. 订单表 (travel_order)
-- ============================================================
CREATE TABLE `travel_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键自增',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号，业务唯一编号',
    `user_id` BIGINT NOT NULL COMMENT '下单用户ID',
    `ticket_id` BIGINT NOT NULL COMMENT '门票ID',
    `scenic_id` BIGINT NOT NULL COMMENT '景点ID',
    `quantity` INT DEFAULT 1 COMMENT '购买数量',
    `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    `contact_name` VARCHAR(50) DEFAULT NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系人电话',
    `visit_date` DATE DEFAULT NULL COMMENT '计划游览日期',
    `status` TINYINT DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已使用，3-已取消，4-已退款',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_ticket_id` (`ticket_id`),
    KEY `idx_scenic_id` (`scenic_id`),
    KEY `idx_status` (`status`),
    KEY `idx_visit_date` (`visit_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================================
-- 9. 攻略表 (strategy)
-- ============================================================
CREATE TABLE `strategy` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '攻略ID，主键自增',
    `user_id` BIGINT NOT NULL COMMENT '发布用户ID',
    `scenic_id` BIGINT DEFAULT NULL COMMENT '关联景点ID，可为空（通用攻略）',
    `title` VARCHAR(200) NOT NULL COMMENT '攻略标题',
    `content` LONGTEXT NOT NULL COMMENT '攻略正文内容',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签，逗号分隔',
    `view_count` BIGINT DEFAULT 0 COMMENT '浏览次数',
    `like_count` BIGINT DEFAULT 0 COMMENT '点赞次数',
    `collect_count` BIGINT DEFAULT 0 COMMENT '收藏次数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-草稿，1-已发布，2-已下架',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_scenic_id` (`scenic_id`),
    KEY `idx_status` (`status`),
    FULLTEXT KEY `ft_title_content` (`title`, `content`) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='攻略表';

-- ============================================================
-- 10. 评论表 (comment)
-- ============================================================
CREATE TABLE `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID，主键自增',
    `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
    `scenic_id` BIGINT NOT NULL COMMENT '关联景点ID',
    `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID，可选',
    `content` VARCHAR(1000) NOT NULL COMMENT '评论内容',
    `rating` TINYINT DEFAULT 5 COMMENT '评分：1-5星',
    `images` VARCHAR(2000) DEFAULT NULL COMMENT '评论图片，JSON数组格式',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID，用于回复',
    `reply_to_user_id` BIGINT DEFAULT NULL COMMENT '被回复用户ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_scenic_id` (`scenic_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ============================================================
-- 11. 用户行为表 (user_behavior)
-- ============================================================
CREATE TABLE `user_behavior` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '行为记录ID，主键自增',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_id` BIGINT NOT NULL COMMENT '目标ID（景点ID/攻略ID等）',
    `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型：scenic-景点，strategy-攻略，ticket-门票',
    `behavior_type` VARCHAR(20) NOT NULL COMMENT '行为类型：view-浏览，collect-收藏，order-下单，like-点赞，search-搜索',
    `behavior_desc` VARCHAR(500) DEFAULT NULL COMMENT '行为描述，如搜索关键词',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '行为发生时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_target` (`target_type`, `target_id`),
    KEY `idx_behavior_type` (`behavior_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表，用于推荐算法和数据分析';

-- ============================================================
-- 12. 敏感词表 (sensitive_word)
-- ============================================================
CREATE TABLE `sensitive_word` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '敏感词ID，主键自增',
    `word` VARCHAR(200) NOT NULL COMMENT '敏感词内容',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类：政治/色情/暴力/广告等',
    `level` TINYINT DEFAULT 1 COMMENT '级别：1-低，2-中，3-高',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_word` (`word`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表，用于Trie树过滤';

-- ============================================================
-- 13. RAG原始文档表 (rag_doc)
-- ============================================================
CREATE TABLE `rag_doc` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文档ID，主键自增',
    `title` VARCHAR(200) NOT NULL COMMENT '文档标题',
    `content` LONGTEXT NOT NULL COMMENT '文档原始内容',
    `source` VARCHAR(200) DEFAULT NULL COMMENT '文档来源',
    `doc_type` VARCHAR(50) DEFAULT 'strategy' COMMENT '文档类型：strategy-攻略/guide-指南/faq-问答',
    `chunk_count` INT DEFAULT 0 COMMENT '分块数量',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-未处理，1-已入库，2-处理失败',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_doc_type` (`doc_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG原始文档表，存储攻略原文';

-- ============================================================
-- 14. RAG分片表 (rag_chunk)
-- ============================================================
CREATE TABLE `rag_chunk` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分片ID，主键自增',
    `doc_id` BIGINT NOT NULL COMMENT '所属文档ID',
    `chunk_index` INT NOT NULL COMMENT '分片序号，从0开始',
    `content` TEXT NOT NULL COMMENT '分片文本内容',
    `vector_id` VARCHAR(100) DEFAULT NULL COMMENT 'Milvus向量ID，用于关联向量库',
    `token_count` INT DEFAULT 0 COMMENT 'token数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_doc_id` (`doc_id`),
    KEY `idx_vector_id` (`vector_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RAG文本分片表，每个分片对应一个向量';

-- ============================================================
-- 15. AI会话表 (ai_session)
-- ============================================================
CREATE TABLE `ai_session` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会话ID，主键自增',
    `session_id` VARCHAR(64) NOT NULL COMMENT '会话唯一标识，UUID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) DEFAULT NULL COMMENT '会话标题，自动取首条问题',
    `message_count` INT DEFAULT 0 COMMENT '消息轮次计数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-已关闭，1-活跃',
    `last_active_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后活跃时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_id` (`session_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI会话表，管理多轮对话上下文';

-- ============================================================
-- 16. 推荐日志表 (recommend_log)
-- ============================================================
CREATE TABLE `recommend_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键自增',
    `user_id` BIGINT NOT NULL COMMENT '被推荐用户ID',
    `scenic_ids` VARCHAR(1000) DEFAULT NULL COMMENT '推荐景点ID列表，逗号分隔',
    `algorithm` VARCHAR(50) DEFAULT 'collaborative_filtering' COMMENT '推荐算法标识',
    `request_params` VARCHAR(500) DEFAULT NULL COMMENT '请求参数快照，JSON格式',
    `result_count` INT DEFAULT 0 COMMENT '返回结果数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '推荐时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_algorithm` (`algorithm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐日志表，记录推荐结果用于效果分析';

-- ============================================================
-- 初始化数据：默认角色和权限
-- ============================================================

-- 插入默认角色
INSERT INTO `role` (`role_name`, `role_desc`) VALUES
('admin', '系统管理员，拥有所有权限'),
('user', '普通用户，拥有基本浏览和操作权限'),
('editor', '内容编辑，可管理攻略和评论');

-- 插入权限（12个业务模块）
INSERT INTO `permission` (`perm_name`, `perm_code`, `perm_desc`, `module`) VALUES
('查看景点', 'scenic:read', '浏览景点列表和详情', 'scenic'),
('管理景点', 'scenic:write', '新增、编辑、上下架景点', 'scenic'),
('查看门票', 'ticket:read', '浏览门票信息', 'ticket'),
('管理门票', 'ticket:write', '新增、编辑门票', 'ticket'),
('查看订单', 'order:read', '查看自己的订单', 'order'),
('管理订单', 'order:write', '创建、取消订单', 'order'),
('查看攻略', 'strategy:read', '浏览攻略列表', 'strategy'),
('发布攻略', 'strategy:write', '发布、编辑攻略', 'strategy'),
('查看评论', 'comment:read', '查看评论列表', 'comment'),
('发表评论', 'comment:write', '发布评论', 'comment'),
('使用AI助手', 'ai:use', '使用AI旅游助手问答', 'ai'),
('管理AI文档', 'ai:admin', '管理RAG文档入库', 'ai'),
('查看推荐', 'recommend:read', '获取个性化推荐', 'recommend'),
('用户管理', 'user:manage', '管理用户账号', 'user'),
('角色管理', 'role:manage', '管理角色和权限分配', 'role'),
('敏感词管理', 'sensitive:manage', '管理敏感词词典', 'sensitive');

-- 为admin角色分配所有权限
INSERT INTO `role_perm` (`role_id`, `perm_id`)
SELECT 1, id FROM `permission`;

-- 为user角色分配基本权限
INSERT INTO `role_perm` (`role_id`, `perm_id`)
SELECT 2, id FROM `permission` WHERE `perm_code` IN (
    'scenic:read', 'ticket:read', 'order:read', 'order:write',
    'strategy:read', 'strategy:write', 'comment:read', 'comment:write',
    'ai:use', 'recommend:read'
);

-- 为editor角色分配内容管理权限
INSERT INTO `role_perm` (`role_id`, `perm_id`)
SELECT 3, id FROM `permission` WHERE `perm_code` IN (
    'scenic:read', 'ticket:read', 'strategy:read', 'strategy:write',
    'comment:read', 'comment:write', 'ai:use', 'recommend:read'
);

-- 插入示例敏感词
INSERT INTO `sensitive_word` (`word`, `category`, `level`) VALUES
('赌博', '违法', 3),
('代开发票', '广告', 2),
('色情', '色情', 3),
('枪支', '暴力', 3);
