"""
协同过滤推荐服务模块
基于用户行为数据实现协同过滤推荐算法
支持浏览、收藏、下单等不同行为类型的加权评分
"""

import logging
from collections import defaultdict
from typing import Any

import numpy as np
from sklearn.metrics.pairwise import cosine_similarity

logger = logging.getLogger(__name__)


class RecommendService:
    """
    协同过滤推荐服务
    基于用户行为数据，通过计算用户间相似度实现推荐
    """

    # 不同行为类型的权重配置
    # 下单行为权重最高，收藏次之，浏览最低
    BEHAVIOR_WEIGHTS: dict[str, float] = {
        "view": 1.0,        # 浏览行为权重
        "favorite": 3.0,    # 收藏行为权重
        "order": 5.0,       # 下单行为权重
    }

    def get_collaborative_recommendations(
        self,
        user_id: str,
        behaviors: list[dict[str, Any]],
        top_n: int = 10,
    ) -> list[dict[str, Any]]:
        """
        基于协同过滤算法获取推荐结果

        算法流程：
        1. 解析用户行为数据，按行为类型赋予不同权重
        2. 构建用户-景点评分矩阵
        3. 计算用户间的余弦相似度
        4. 找到 TopK 最相似的用户
        5. 基于相似用户的偏好计算推荐分数
        6. 排序并返回 TopN 推荐结果

        Args:
            user_id: 目标用户 ID
            behaviors: 用户行为数据列表，每条记录包含 user_id、spot_id、behavior_type
            top_n: 返回的推荐数量，默认 10

        Returns:
            list[dict]: 推荐结果列表，每个元素包含 spot_id 和推荐分数
        """
        # 第一步：解析行为数据，构建用户-景点加权评分
        # user_spot_scores[user][spot] = 加权评分
        user_spot_scores: dict[str, dict[str, float]] = defaultdict(
            lambda: defaultdict(float)
        )

        for behavior in behaviors:
            # 提取行为记录中的用户 ID、景点 ID 和行为类型
            b_user_id = behavior.get("user_id", "")
            spot_id = behavior.get("spot_id", "")
            behavior_type = behavior.get("behavior_type", "view")

            # 根据行为类型获取对应权重，未知行为类型使用默认权重 1.0
            weight = self.BEHAVIOR_WEIGHTS.get(behavior_type, 1.0)

            # 累加加权评分（同一用户对同一景点的多次行为会叠加）
            user_spot_scores[b_user_id][spot_id] += weight

        # 检查是否有足够的用户数据
        if len(user_spot_scores) < 2:
            logger.warning("用户行为数据不足，无法进行协同过滤推荐")
            return []

        # 第二步：构建用户-景点评分矩阵
        # 收集所有出现过的景点 ID，作为矩阵的列
        all_spot_ids = sorted({
            spot_id
            for user_scores in user_spot_scores.values()
            for spot_id in user_scores
        })

        # 收集所有用户 ID，作为矩阵的行
        all_user_ids = sorted(user_spot_scores.keys())

        # 创建 spot_id 到列索引的映射
        spot_index_map = {spot_id: i for i, spot_id in enumerate(all_spot_ids)}
        # 创建 user_id 到行索引的映射
        user_index_map = {uid: i for i, uid in enumerate(all_user_ids)}

        # 初始化评分矩阵，形状为 (用户数, 景点数)
        n_users = len(all_user_ids)
        n_spots = len(all_spot_ids)
        rating_matrix = np.zeros((n_users, n_spots))

        # 填充评分矩阵
        for uid, spot_scores in user_spot_scores.items():
            user_idx = user_index_map[uid]
            for spot_id, score in spot_scores.items():
                spot_idx = spot_index_map[spot_id]
                rating_matrix[user_idx][spot_idx] = score

        # 第三步：计算用户间的余弦相似度
        # cosine_similarity 返回形状为 (n_users, n_users) 的相似度矩阵
        user_similarity_matrix = cosine_similarity(rating_matrix)

        # 第四步：找到目标用户的 TopK 相似用户
        # 获取目标用户在矩阵中的行索引
        if user_id not in user_index_map:
            # 目标用户不在行为数据中，返回空结果
            logger.warning(f"目标用户 {user_id} 不在行为数据中")
            return []

        target_user_idx = user_index_map[user_id]

        # 获取目标用户与其他所有用户的相似度
        similarities = user_similarity_matrix[target_user_idx]

        # 排除目标用户自身（相似度为 1.0），选择 TopK 最相似用户
        # 取 Top 20 相似用户，平衡推荐质量和计算开销
        top_k_similar = min(20, n_users - 1)
        similar_user_indices = np.argsort(similarities)[::-1]
        # 过滤掉目标用户自身
        similar_user_indices = [
            idx for idx in similar_user_indices
            if idx != target_user_idx
        ][:top_k_similar]

        # 第五步：基于相似用户的偏好计算推荐分数
        # 对每个景点，计算加权推荐分数 = sum(相似度 * 评分) / sum(相似度)
        recommendation_scores: dict[str, float] = defaultdict(float)

        # 获取目标用户已经交互过的景点（避免重复推荐）
        target_user_spots = set(user_spot_scores[user_id].keys())

        for similar_idx in similar_user_indices:
            similar_user_id = all_user_ids[similar_idx]
            # 该相似用户与目标用户的相似度
            sim_score = similarities[similar_idx]

            # 相似度为 0 或负值的用户不参与推荐
            if sim_score <= 0:
                continue

            # 遍历相似用户评分过的景点
            for spot_id, score in user_spot_scores[similar_user_id].items():
                # 跳过目标用户已经交互过的景点
                if spot_id in target_user_spots:
                    continue
                # 推荐分数 = 用户相似度 * 该用户对该景点的评分
                # 相似度越高的用户，其偏好对推荐结果的影响越大
                recommendation_scores[spot_id] += sim_score * score

        # 第六步：按推荐分数降序排序，返回 TopN 结果
        sorted_recommendations = sorted(
            recommendation_scores.items(),
            key=lambda x: x[1],
            reverse=True,
        )[:top_n]

        # 构建返回结果
        results = [
            {
                "spot_id": spot_id,
                "score": round(float(score), 4),
            }
            for spot_id, score in sorted_recommendations
        ]

        logger.info(
            f"协同过滤推荐完成，用户 {user_id}，"
            f"找到 {len(similar_user_indices)} 个相似用户，"
            f"返回 {len(results)} 个推荐结果"
        )

        return results
