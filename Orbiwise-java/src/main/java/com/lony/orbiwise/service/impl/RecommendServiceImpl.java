package com.lony.orbiwise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lony.orbiwise.entity.RecommendLog;
import com.lony.orbiwise.entity.Scenic;
import com.lony.orbiwise.entity.UserBehavior;
import com.lony.orbiwise.mapper.RecommendLogMapper;
import com.lony.orbiwise.mapper.ScenicMapper;
import com.lony.orbiwise.mapper.UserBehaviorMapper;
import com.lony.orbiwise.service.RecommendService;
import com.lony.orbiwise.util.Constants;
import com.lony.orbiwise.vo.RecommendVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类
 * <p>基于协同过滤（Collaborative Filtering）算法实现个性化景点推荐。
 * 核心思路：通过用户行为数据构建评分矩阵，使用余弦相似度计算用户间相似度，
 * 找到最相似的 K 个用户，基于相似用户的偏好预测目标用户对景点的评分。</p>
 *
 * @author lin504
 */
@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private ScenicMapper scenicMapper;

    @Autowired
    private RecommendLogMapper recommendLogMapper;

    /**
     * {@inheritDoc}
     * <p>协同过滤推荐算法实现步骤：
     * <ol>
     *   <li>获取目标用户的行为数据</li>
     *   <li>构建所有用户的评分矩阵</li>
     *   <li>计算用户间余弦相似度</li>
     *   <li>找到最相似的 K 个用户</li>
     *   <li>基于相似用户偏好计算推荐分数</li>
     *   <li>排序返回 TopN 推荐结果</li>
     *   <li>记录推荐日志</li>
     * </ol>
     * </p>
     */
    @Override
    public List<RecommendVO> getRecommendations(Long userId, Integer topN) {
        // ========== 步骤1：获取目标用户的行为数据 ==========
        // 查询目标用户的所有行为记录（浏览、收藏、下单、点赞）
        LambdaQueryWrapper<UserBehavior> targetQuery = new LambdaQueryWrapper<>();
        targetQuery.eq(UserBehavior::getUserId, userId)
                .eq(UserBehavior::getTargetType, "scenic");
        List<UserBehavior> targetBehaviors = userBehaviorMapper.selectList(targetQuery);

        // 如果目标用户没有任何行为数据，返回热门景点作为默认推荐
        if (targetBehaviors.isEmpty()) {
            return getDefaultRecommendations(topN);
        }

        // ========== 步骤2：构建用户-景点评分矩阵 ==========
        // 查询所有用户对景点的行为数据
        List<UserBehavior> allBehaviors = userBehaviorMapper.selectList(
                new LambdaQueryWrapper<UserBehavior>()
                        .eq(UserBehavior::getTargetType, "scenic"));

        // 构建评分矩阵：Map<userId, Map<scenicId, score>>
        // 每个用户对景点的评分由行为类型加权计算：
        //   浏览(view) = 1分, 点赞(like) = 2分, 收藏(collect) = 3分, 下单(order) = 5分
        Map<Long, Map<Long, Double>> userScenicMatrix = new HashMap<>();
        for (UserBehavior behavior : allBehaviors) {
            Long uid = behavior.getUserId();
            Long scenicId = behavior.getTargetId();
            double weight = getBehaviorWeight(behavior.getBehaviorType());

            // 累加同一用户对同一景点的评分
            userScenicMatrix.computeIfAbsent(uid, k -> new HashMap<>());
            userScenicMatrix.get(uid).merge(scenicId, weight, Double::sum);
        }

        // ========== 步骤3：计算用户间余弦相似度 ==========
        // 余弦相似度公式：sim(A, B) = dot(A, B) / (|A| * |B|)
        // 其中 dot(A, B) 是两个用户评分向量的点积，|A| 和 |B| 是向量的模
        Map<Long, Double> similarities = new HashMap<>();
        Map<Long, Double> targetVector = userScenicMatrix.getOrDefault(userId, new HashMap<>());

        for (Map.Entry<Long, Map<Long, Double>> entry : userScenicMatrix.entrySet()) {
            Long otherUserId = entry.getKey();
            // 跳过目标用户自己
            if (otherUserId.equals(userId)) {
                continue;
            }

            Map<Long, Double> otherVector = entry.getValue();

            // 计算点积：两个用户共同评价过的景点的评分乘积之和
            double dotProduct = 0.0;
            for (Map.Entry<Long, Double> targetEntry : targetVector.entrySet()) {
                Double otherScore = otherVector.get(targetEntry.getKey());
                if (otherScore != null) {
                    // 只有两个用户都评价过的景点才计入点积
                    dotProduct += targetEntry.getValue() * otherScore;
                }
            }

            // 计算两个向量的模（欧几里得范数）
            double targetMagnitude = Math.sqrt(
                    targetVector.values().stream().mapToDouble(v -> v * v).sum());
            double otherMagnitude = Math.sqrt(
                    otherVector.values().stream().mapToDouble(v -> v * v).sum());

            // 计算余弦相似度，避免除零
            if (targetMagnitude > 0 && otherMagnitude > 0) {
                similarities.put(otherUserId, dotProduct / (targetMagnitude * otherMagnitude));
            }
        }

        // ========== 步骤4：找到最相似的 K 个用户 ==========
        // 按相似度降序排序，取前 K 个最相似用户
        int k = Constants.CF_K_NEIGHBORS;
        List<Map.Entry<Long, Double>> sortedSimilarities = new ArrayList<>(similarities.entrySet());
        sortedSimilarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        List<Map.Entry<Long, Double>> topKNeighbors = sortedSimilarities.subList(
                0, Math.min(k, sortedSimilarities.size()));

        // ========== 步骤5：基于相似用户偏好计算推荐分数 ==========
        // 对每个候选景点，计算加权评分：
        //   score(scenic) = sum(sim(user, neighbor) * neighbor_score(scenic)) / sum(|sim|)
        Map<Long, Double> candidateScores = new HashMap<>();

        // 获取目标用户已经交互过的景点ID，推荐时需要排除
        Set<Long> interactedScenicIds = targetVector.keySet();

        for (Map.Entry<Long, Double> neighbor : topKNeighbors) {
            Long neighborId = neighbor.getKey();
            double similarity = neighbor.getValue();
            Map<Long, Double> neighborScores = userScenicMatrix.get(neighborId);

            for (Map.Entry<Long, Double> scoreEntry : neighborScores.entrySet()) {
                Long scenicId = scoreEntry.getKey();
                // 排除目标用户已经交互过的景点
                if (!interactedScenicIds.contains(scenicId)) {
                    // 加权评分 = 相似度 * 邻居对该景点的评分
                    candidateScores.merge(scenicId, similarity * scoreEntry.getValue(), Double::sum);
                }
            }
        }

        // ========== 步骤6：排序返回 TopN 推荐结果 ==========
        List<Map.Entry<Long, Double>> sortedScores = new ArrayList<>(candidateScores.entrySet());
        sortedScores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<RecommendVO> recommendations = new ArrayList<>();
        int resultCount = Math.min(topN, sortedScores.size());
        for (int i = 0; i < resultCount; i++) {
            Map.Entry<Long, Double> entry = sortedScores.get(i);
            Long scenicId = entry.getKey();
            double score = entry.getValue();

            // 查询景点信息
            Scenic scenic = scenicMapper.selectById(scenicId);
            if (scenic != null) {
                RecommendVO vo = new RecommendVO();
                vo.setScenicId(scenicId);
                vo.setName(scenic.getName());
                vo.setScore(Math.round(score * 100.0) / 100.0);
                vo.setReason("基于协同过滤算法推荐，与您兴趣相似的用户也喜欢此景点");
                recommendations.add(vo);
            }
        }

        // ========== 步骤7：记录推荐日志 ==========
        RecommendLog log = new RecommendLog();
        log.setUserId(userId);
        log.setScenicIds(recommendations.stream()
                .map(r -> String.valueOf(r.getScenicId()))
                .collect(Collectors.joining(",")));
        log.setAlgorithm("collaborative_filtering");
        log.setRequestParams("{\"topN\":" + topN + ",\"kNeighbors\":" + k + "}");
        log.setResultCount(recommendations.size());
        log.setCreateTime(LocalDateTime.now());
        recommendLogMapper.insert(log);

        return recommendations;
    }

    /**
     * 获取行为类型对应的权重值
     * <p>不同行为类型代表不同的兴趣强度：
     * 下单(5) > 收藏(3) > 点赞(2) > 浏览(1)</p>
     *
     * @param behaviorType 行为类型
     * @return 行为权重
     */
    private double getBehaviorWeight(String behaviorType) {
        return switch (behaviorType) {
            case "order" -> Constants.BEHAVIOR_WEIGHT_ORDER;
            case "collect" -> Constants.BEHAVIOR_WEIGHT_COLLECT;
            case "like" -> Constants.BEHAVIOR_WEIGHT_LIKE;
            case "view" -> Constants.BEHAVIOR_WEIGHT_VIEW;
            default -> Constants.BEHAVIOR_WEIGHT_VIEW;
        };
    }

    /**
     * 获取默认推荐（用户无行为数据时）
     * <p>按浏览次数降序返回热门景点</p>
     *
     * @param topN 推荐数量
     * @return 默认推荐列表
     */
    private List<RecommendVO> getDefaultRecommendations(Integer topN) {
        LambdaQueryWrapper<Scenic> query = new LambdaQueryWrapper<>();
        query.eq(Scenic::getStatus, Constants.SCENIC_STATUS_ON)
                .orderByDesc(Scenic::getViewCount)
                .last("LIMIT " + topN);
        List<Scenic> hotScenics = scenicMapper.selectList(query);

        return hotScenics.stream().map(scenic -> {
            RecommendVO vo = new RecommendVO();
            vo.setScenicId(scenic.getId());
            vo.setName(scenic.getName());
            vo.setScore(scenic.getViewCount().doubleValue());
            vo.setReason("热门景点推荐");
            return vo;
        }).collect(Collectors.toList());
    }

}
