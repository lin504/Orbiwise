"""
数据模型定义模块
定义所有 API 接口的请求体和响应体 Pydantic 模型
确保数据校验和序列化的一致性
"""

from typing import Optional

from pydantic import BaseModel, Field


class RagStoreRequest(BaseModel):
    """
    文档入库请求模型
    接收旅游攻略的标题和内容，进行分块向量化后存入 Milvus
    """
    # 攻略标题
    title: str = Field(..., description="攻略标题", examples=["三亚五日游攻略"])
    # 攻略正文内容
    content: str = Field(..., description="攻略正文内容")
    # 攻略来源（可选，如 URL、书名等）
    source: Optional[str] = Field(None, description="攻略来源", examples=["https://example.com/article/123"])
    # 文档类型，默认为攻略类型
    doc_type: str = Field("strategy", description="文档类型", examples=["strategy"])


class RagChatRequest(BaseModel):
    """
    RAG 问答请求模型
    接收用户问题和会话 ID，基于 RAG 架构生成回答
    """
    # 用户提出的问题
    question: str = Field(..., description="用户提出的问题", examples=["三亚有什么好吃的？"])
    # 会话 ID，用于维护多轮对话上下文
    session_id: str = Field(..., description="会话 ID，用于维护多轮对话上下文", examples=["session_abc123"])


class ClearSessionRequest(BaseModel):
    """
    清空会话请求模型
    根据会话 ID 清除 Redis 中存储的对话历史
    """
    # 需要清空的会话 ID
    session_id: str = Field(..., description="需要清空的会话 ID", examples=["session_abc123"])


class RagResponse(BaseModel):
    """
    RAG 问答响应模型
    包含 AI 生成的回答、引用的攻略来源和会话 ID
    """
    # AI 生成的回答内容
    answer: str = Field(..., description="AI 生成的回答内容")
    # 回答中引用的攻略片段列表
    sources: list[str] = Field(default_factory=list, description="引用的攻略片段列表")
    # 当前会话 ID
    session_id: str = Field(..., description="当前会话 ID")


class StoreResponse(BaseModel):
    """
    文档入库响应模型
    返回入库操作的结果信息
    """
    # 文档唯一标识
    doc_id: str = Field(..., description="文档唯一标识")
    # 文档被切分后的块数量
    chunk_count: int = Field(..., description="文档被切分后的块数量")
    # 操作结果消息
    message: str = Field(..., description="操作结果消息")


class HealthResponse(BaseModel):
    """
    健康检查响应模型
    返回服务及各依赖组件的运行状态
    """
    # 服务状态
    status: str = Field(..., description="服务状态", examples=["healthy"])
    # Milvus 连接状态
    milvus_connected: bool = Field(..., description="Milvus 是否连接正常")
    # Redis 连接状态
    redis_connected: bool = Field(..., description="Redis 是否连接正常")
