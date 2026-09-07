package com.lony.orbiwise.service;

import com.lony.orbiwise.vo.RecommendVO;

import java.util.List;

/**
 * 推荐服务接口
 * <p>提供基于协同过滤算法的个性化景点推荐</p>
 *
 * @author lin504
 */
public interface RecommendService {

    /**
     * 获取个性化推荐
     * <p>基于协同过滤推荐算法，分析用户行为数据，计算用户相似度，返回推荐景点列表</p>
     *
     * @param userId 用户ID
     * @param topN   推荐数量
     * @return 推荐结果列表
     */
    List<RecommendVO> getRecommendations(Long userId, Integer topN);

}
