"""
智能推荐路由模块
定义协同过滤推荐相关的 API 接口
"""

import logging
from typing import Any

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field

from services.recommend_service import RecommendService

logger = logging.getLogger(__name__)

# 创建推荐路由器，设置路径前缀和标签
router = APIRouter(prefix="/api/recommend", tags=["智能推荐"])

# 全局推荐服务实例引用
_recommend_service: RecommendService | None = None


def set_recommend_service(recommend_service: RecommendService) -> None:
    """
    设置推荐服务实例
    在应用启动时由 main.py 调用
    """
    global _recommend_service
    _recommend_service = recommend_service


def get_recommend_service() -> RecommendService:
    """
    获取推荐服务实例
    """
    if _recommend_service is None:
        raise HTTPException(status_code=503, detail="推荐服务尚未初始化")
    return _recommend_service


class CollaborativeRequest(BaseModel):
    """
    协同过滤推荐请求模型
    """
    # 目标用户 ID
    user_id: str = Field(..., description="目标用户 ID", examples=["user_001"])
    # 用户行为数据列表
    behaviors: list[dict[str, Any]] = Field(
        ...,
        description="用户行为数据，每条包含 user_id、spot_id、behavior_type",
        examples=[[
            {"user_id": "user_001", "spot_id": "spot_001", "behavior_type": "view"},
            {"user_id": "user_002", "spot_id": "spot_001", "behavior_type": "order"},
        ]],
    )
    # 返回推荐数量
    top_n: int = Field(10, description="返回推荐数量", ge=1, le=50)


class CollaborativeResponse(BaseModel):
    """
    协同过滤推荐响应模型
    """
    # 推荐结果列表
    recommendations: list[dict[str, Any]] = Field(
        default_factory=list,
        description="推荐结果列表，包含 spot_id 和 score",
    )
    # 推荐结果数量
    count: int = Field(..., description="推荐结果数量")


@router.post("/collaborative", response_model=CollaborativeResponse)
async def collaborative_recommend(request: CollaborativeRequest) -> CollaborativeResponse:
    """
    协同过滤推荐接口

    基于用户行为数据（浏览、收藏、下单），通过协同过滤算法
    计算用户间相似度，为目标用户推荐可能感兴趣的景点。

    参数:
        request: 推荐请求体，包含以下字段：
            - user_id: 目标用户 ID
            - behaviors: 用户行为数据列表
            - top_n: 返回推荐数量（默认 10）

    返回:
        CollaborativeResponse: 推荐结果，包含：
            - recommendations: 推荐景点列表（含 spot_id 和 score）
            - count: 推荐结果数量
    """
    try:
        recommend_service = get_recommend_service()
        results = recommend_service.get_collaborative_recommendations(
            user_id=request.user_id,
            behaviors=request.behaviors,
            top_n=request.top_n,
        )
        return CollaborativeResponse(
            recommendations=results,
            count=len(results),
        )
    except Exception as e:
        logger.error(f"协同过滤推荐失败: {e}")
        raise HTTPException(status_code=500, detail=f"推荐失败: {str(e)}")
