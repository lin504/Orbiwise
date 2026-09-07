package com.lony.orbiwise.service;

import com.lony.orbiwise.dto.StrategyDTO;
import com.lony.orbiwise.vo.PageVO;
import com.lony.orbiwise.vo.StrategyVO;

/**
 * 旅游攻略服务接口
 * <p>提供攻略的查询、发布、点赞、收藏等功能</p>
 *
 * @author lin504
 */
public interface StrategyService {

    /**
     * 分页查询攻略列表
     *
     * @param page     页码
     * @param size     每页大小
     * @param scenicId 景点ID筛选（可选）
     * @param keyword  关键词搜索（可选）
     * @return 分页攻略列表
     */
    PageVO<StrategyVO> listStrategy(Integer page, Integer size, Long scenicId, String keyword);

    /**
     * 获取攻略详情
     *
     * @param id 攻略ID
     * @return 攻略详情
     */
    StrategyVO getStrategyDetail(Long id);

    /**
     * 发布攻略
     *
     * @param userId     用户ID
     * @param strategyDTO 攻略参数
     * @return 攻略ID
     */
    Long publishStrategy(Long userId, StrategyDTO strategyDTO);

    /**
     * 点赞攻略
     *
     * @param userId    用户ID
     * @param strategyId 攻略ID
     */
    void likeStrategy(Long userId, Long strategyId);

    /**
     * 收藏攻略
     *
     * @param userId    用户ID
     * @param strategyId 攻略ID
     */
    void collectStrategy(Long userId, Long strategyId);

}
