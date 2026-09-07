"""
RAG 路由模块
定义 RAG 旅游问答相关的 API 接口
包括文档入库、RAG 问答和会话清空
"""

import logging
from typing import TYPE_CHECKING

from fastapi import APIRouter, HTTPException

from models.schemas import (
    ClearSessionRequest,
    RagChatRequest,
    RagResponse,
    RagStoreRequest,
    StoreResponse,
)

if TYPE_CHECKING:
    from services.rag_service import RagService

logger = logging.getLogger(__name__)

# 创建 RAG 路由器，设置路径前缀和标签
router = APIRouter(prefix="/api/rag", tags=["RAG AI旅游助手"])

# 全局 RAG 服务实例引用，在 main.py 启动时注入
_rag_service: "RagService | None" = None


def set_rag_service(rag_service: "RagService") -> None:
    """
    设置 RAG 服务实例
    在应用启动时由 main.py 调用，注入已初始化的服务实例
    """
    global _rag_service
    _rag_service = rag_service


def get_rag_service() -> "RagService":
    """
    获取 RAG 服务实例
    如果服务未初始化则抛出异常
    """
    if _rag_service is None:
        raise HTTPException(status_code=503, detail="RAG 服务尚未初始化")
    return _rag_service


@router.post("/store", response_model=StoreResponse)
async def store_document(request: RagStoreRequest) -> StoreResponse:
    """
    旅游攻略文档入库接口

    将旅游攻略文本进行分块、向量化后存入 Milvus 向量数据库，
    供后续 RAG 问答时检索使用。

    参数:
        request: 入库请求体，包含以下字段：
            - title: 攻略标题
            - content: 攻略正文内容
            - source: 攻略来源（可选）
            - doc_type: 文档类型（默认 "strategy"）

    返回:
        StoreResponse: 入库结果，包含：
            - doc_id: 文档唯一标识
            - chunk_count: 分块数量
            - message: 操作结果消息
    """
    try:
        rag_service = get_rag_service()
        result = rag_service.store_document(request)
        return result
    except Exception as e:
        logger.error(f"文档入库失败: {e}")
        raise HTTPException(status_code=500, detail=f"文档入库失败: {str(e)}")


@router.post("/chat", response_model=RagResponse)
async def chat(request: RagChatRequest) -> RagResponse:
    """
    RAG 智能问答接口

    基于检索增强生成（RAG）架构，先从向量数据库中召回相关攻略片段，
    再结合对话历史调用大模型生成回答。

    参数:
        request: 问答请求体，包含以下字段：
            - question: 用户提出的问题
            - session_id: 会话 ID，用于维护多轮对话上下文

    返回:
        RagResponse: 问答响应，包含：
            - answer: AI 生成的回答
            - sources: 引用的攻略片段列表
            - session_id: 当前会话 ID
    """
    try:
        rag_service = get_rag_service()
        result = rag_service.chat(request)
        return result
    except Exception as e:
        logger.error(f"RAG 问答失败: {e}")
        raise HTTPException(status_code=500, detail=f"RAG 问答失败: {str(e)}")


@router.post("/clear_session")
async def clear_session(request: ClearSessionRequest) -> dict:
    """
    清空会话历史接口

    清除 Redis 中指定会话 ID 的对话历史记录，
    用于重置对话上下文或释放存储空间。

    参数:
        request: 清空请求体，包含以下字段：
            - session_id: 需要清空的会话 ID

    返回:
        dict: 操作结果消息
    """
    try:
        rag_service = get_rag_service()
        rag_service.clear_session(request.session_id)
        return {"message": f"会话 {request.session_id} 已清空"}
    except Exception as e:
        logger.error(f"清空会话失败: {e}")
        raise HTTPException(status_code=500, detail=f"清空会话失败: {str(e)}")
