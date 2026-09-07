package com.lony.orbiwise.util;

/**
 * 常量类
 * <p>定义系统中使用的各类常量，避免魔法值</p>
 *
 * @author lin504
 */
public final class Constants {

    private Constants() {
        // 防止实例化
    }

    // ========== Redis Key 前缀 ==========

    /** Redis中敏感词Trie树的缓存key */
    public static final String REDIS_SENSITIVE_WORD_KEY = "orbiwise:sensitive:words";

    /** Redis中用户信息的缓存key前缀 */
    public static final String REDIS_USER_KEY_PREFIX = "orbiwise:user:";

    /** Redis中景点详情的缓存key前缀 */
    public static final String REDIS_SCENIC_KEY_PREFIX = "orbiwise:scenic:";

    /** Redis中推荐结果的缓存key前缀 */
    public static final String REDIS_RECOMMEND_KEY_PREFIX = "orbiwise:recommend:";

    // ========== Token 相关 ==========

    /** HTTP Header 中的 Authorization 字段名 */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /** Token 前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    // ========== 分页相关 ==========

    /** 默认分页大小 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** 最大分页大小 */
    public static final int MAX_PAGE_SIZE = 100;

    /** 默认页码 */
    public static final int DEFAULT_PAGE_NUM = 1;

    // ========== 用户状态 ==========

    /** 用户状态：正常 */
    public static final int USER_STATUS_NORMAL = 1;

    /** 用户状态：禁用 */
    public static final int USER_STATUS_DISABLED = 0;

    // ========== 订单状态 ==========

    /** 订单状态：待支付 */
    public static final int ORDER_STATUS_PENDING = 0;

    /** 订单状态：已支付 */
    public static final int ORDER_STATUS_PAID = 1;

    /** 订单状态：已取消 */
    public static final int ORDER_STATUS_CANCELLED = 2;

    /** 订单状态：已退款 */
    public static final int ORDER_STATUS_REFUNDED = 3;

    // ========== 推荐算法相关 ==========

    /** 协同过滤最近邻用户数 K */
    public static final int CF_K_NEIGHBORS = 5;

    /** 默认推荐数量 */
    public static final int DEFAULT_RECOMMEND_TOP_N = 10;

    /** 行为权重：浏览 */
    public static final double BEHAVIOR_WEIGHT_VIEW = 1.0;

    /** 行为权重：收藏 */
    public static final double BEHAVIOR_WEIGHT_COLLECT = 3.0;

    /** 行为权重：下单 */
    public static final double BEHAVIOR_WEIGHT_ORDER = 5.0;

    /** 行为权重：点赞 */
    public static final double BEHAVIOR_WEIGHT_LIKE = 2.0;

    // ========== 请求属性 ==========

    /** Request中存储的用户ID属性名 */
    public static final String REQUEST_ATTR_USER_ID = "userId";

    /** Request中存储的用户名属性名 */
    public static final String REQUEST_ATTR_USERNAME = "username";

    /** Request中存储的角色列表属性名 */
    public static final String REQUEST_ATTR_ROLES = "roles";

    // ========== 评论状态 ==========

    /** 评论状态：已通过 */
    public static final int COMMENT_STATUS_APPROVED = 1;

    // ========== 攻略状态 ==========

    /** 攻略状态：已发布 */
    public static final int STRATEGY_STATUS_PUBLISHED = 1;

    // ========== 景点状态 ==========

    /** 景点状态：上架 */
    public static final int SCENIC_STATUS_ON = 1;

    // ========== 敏感词状态 ==========

    /** 敏感词状态：启用 */
    public static final int SENSITIVE_WORD_ENABLED = 1;

}
