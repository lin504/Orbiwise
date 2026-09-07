"""
RAG 核心服务模块
实现检索增强生成（Retrieval-Augmented Generation）的完整流程
包括文档入库、向量化存储、上下文检索和 LLM 对话生成
"""

import logging
import uuid
from typing import Any

import redis
from langchain_community.chat_message_histories import RedisChatMessageHistory
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_openai import ChatOpenAI

from config.settings import settings
from models.schemas import (
    RagChatRequest,
    RagResponse,
    RagStoreRequest,
    StoreResponse,
)
from services.chunk_service import ChunkService
from services.embedding_service import EmbeddingService
from services.vector_store_service import VectorStoreService

logger = logging.getLogger(__name__)


class RagService:
    """
    RAG 核心服务
    协调 Embedding、向量存储、文本分块和 LLM 对话，实现完整的 RAG 流程
    """

    def __init__(
        self,
        embedding_service: EmbeddingService,
        vector_store_service: VectorStoreService,
    ) -> None:
        """
        初始化 RAG 服务

        Args:
            embedding_service: Embedding 向量化服务实例
            vector_store_service: Milvus 向量存储服务实例
        """
        # 保存 Embedding 服务引用
        self.embedding_service = embedding_service
        # 保存向量存储服务引用
        self.vector_store_service = vector_store_service
        # 初始化文本分块服务
        self.chunk_service = ChunkService()

        # 初始化 Redis 连接，用于存储对话历史
        self.redis_client = redis.Redis(
            host=settings.REDIS_HOST,
            port=settings.REDIS_PORT,
            db=settings.REDIS_DB,
            decode_responses=True,
        )

        # 配置 LangChain LLM（使用 OpenAI 兼容接口）
        self.llm = ChatOpenAI(
            # 模型名称
            model=settings.LLM_MODEL_NAME,
            # API 密钥
            openai_api_key=settings.LLM_API_KEY,
            # API 基础地址（支持第三方兼容接口）
            openai_api_base=settings.LLM_API_BASE,
            # 生成温度，控制回答的随机性
            temperature=0.7,
        )

        # 构建 RAG 对话 Prompt 模板
        self.prompt = ChatPromptTemplate.from_messages([
            # 系统提示词，设定 AI 的角色和回答规范
            SystemMessage(content=(
                "你是一个专业的旅游助手，基于提供的旅游攻略信息来回答用户的问题。"
                "请根据以下攻略内容给出准确、有用的回答。"
                "如果攻略内容中没有相关信息，请诚实告知用户。"
                "回答时请引用具体的攻略来源。\n\n"
                "攻略参考内容：\n{context}"
            )),
            # 对话历史占位符，用于维护多轮对话上下文
            MessagesPlaceholder(variable_name="chat_history"),
            # 用户当前问题的占位符
            ("human", "{question}"),
        ])

        logger.info("RAG 核心服务初始化完成")

    def store_document(self, request: RagStoreRequest) -> StoreResponse:
        """
        文档入库流程
        1. 将攻略文本进行分块处理
        2. 对每个文本块进行向量化
        3. 将向量和元数据存入 Milvus

        Args:
            request: 文档入库请求，包含标题、内容和来源

        Returns:
            StoreResponse: 入库结果，包含文档 ID、分块数量和状态消息
        """
        # 生成文档唯一标识
        doc_id = str(uuid.uuid4())

        # 第一步：将标题和内容拼接，作为完整的文本进行处理
        # 标题前缀有助于在检索时提供上下文信息
        full_text = f"{request.title}\n\n{request.content}"

        # 第二步：调用分块服务将长文本切分为适合向量化的片段
        # 默认 chunk_size=500, overlap=50，保证语义连贯
        chunks = self.chunk_service.chunk_text(full_text)

        if not chunks:
            # 文本为空或分块失败，返回错误信息
            return StoreResponse(
                doc_id=doc_id,
                chunk_count=0,
                message="文档内容为空，无法入库",
            )

        # 第三步：调用 Embedding 服务将所有文本块批量向量化
        # 批量编码比逐条编码效率更高
        embeddings = self.embedding_service.encode(chunks)

        # 第四步：准备文档 ID 列表和分块序号列表
        # 每个分块都关联到同一个 doc_id，便于后续按文档删除
        doc_ids = [doc_id] * len(chunks)
        chunk_indices = list(range(len(chunks)))

        # 第五步：将向量和元数据批量插入 Milvus 向量数据库
        self.vector_store_service.insert_vectors(
            embeddings=embeddings,
            doc_ids=doc_ids,
            chunk_texts=chunks,
            chunk_indices=chunk_indices,
        )

        logger.info(f"文档 '{request.title}' 入库成功，doc_id={doc_id}，共 {len(chunks)} 个分块")

        # 返回入库结果
        return StoreResponse(
            doc_id=doc_id,
            chunk_count=len(chunks),
            message=f"文档入库成功，共分为 {len(chunks)} 个片段",
        )

    def chat(self, request: RagChatRequest) -> RagResponse:
        """
        RAG 问答流程
        1. 将用户问题向量化
        2. 从 Milvus 召回相关攻略片段
        3. 构建包含上下文的 Prompt
        4. 使用 LangChain 管理对话历史
        5. 调用 LLM 生成回答

        Args:
            request: 问答请求，包含用户问题和会话 ID

        Returns:
            RagResponse: 包含 AI 回答、引用来源和会话 ID
        """
        # 第一步：将用户问题转换为向量表示
        # 用于在 Milvus 中进行相似度搜索
        query_embedding = self.embedding_service.encode_single(request.question)

        # 第二步：从 Milvus 向量数据库中召回最相关的攻略片段
        # top_k=5 表示返回最相似的 5 个片段作为上下文
        relevant_chunks = self.vector_store_service.search_vectors(
            query_embedding=query_embedding,
            top_k=5,
        )

        # 第三步：构建上下文文本
        # 将召回的攻略片段拼接为结构化的参考内容
        context = self._build_context(relevant_chunks)

        # 第四步：获取或创建对话历史
        # 使用 Redis 作为后端存储，key 为 session_id
        # LangChain 的 RedisChatMessageHistory 自动处理序列化
        chat_history = self._get_chat_history(request.session_id)

        # 第五步：构建完整的 Prompt 并调用 LLM
        # 将上下文、对话历史和用户问题组合发送给 LLM
        messages = self.prompt.format_messages(
            context=context,
            chat_history=chat_history,
            question=request.question,
        )

        # 调用 LLM 生成回答
        response = self.llm.invoke(messages)
        answer = response.content

        # 第六步：将本轮对话（用户问题和 AI 回答）保存到 Redis 对话历史
        # 这样下一轮对话时 LLM 能看到之前的上下文
        chat_history.append(HumanMessage(content=request.question))
        chat_history.append(AIMessage(content=answer))

        # 提取引用的攻略来源（去重）
        sources = self._extract_sources(relevant_chunks)

        logger.info(
            f"RAG 问答完成，session_id={request.session_id}，"
            f"召回 {len(relevant_chunks)} 个片段"
        )

        # 返回 RAG 问答响应
        return RagResponse(
            answer=answer,
            sources=sources,
            session_id=request.session_id,
        )

    def clear_session(self, session_id: str) -> None:
        """
        清空指定会话的对话历史

        删除 Redis 中以 session_id 为 key 的所有对话记录

        Args:
            session_id: 要清空的会话 ID
        """
        # 构建 Redis key，添加前缀避免与其他数据冲突
        redis_key = f"chat_history:{session_id}"

        # 从 Redis 中删除该 key
        self.redis_client.delete(redis_key)

        logger.info(f"已清空会话历史，session_id={session_id}")

    def _build_context(self, chunks: list[dict[str, Any]]) -> str:
        """
        构建 RAG 上下文文本

        将召回的攻略片段格式化为结构化的参考内容
        每个片段标注序号和相似度分数，便于 LLM 参考

        Args:
            chunks: 召回的攻略片段列表

        Returns:
            str: 格式化后的上下文文本
        """
        if not chunks:
            return "暂无相关攻略信息"

        context_parts: list[str] = []
        for i, chunk in enumerate(chunks, 1):
            # 格式化每个攻略片段，包含序号和相似度分数
            chunk_text = chunk.get("chunk_text", "")
            score = chunk.get("score", 0)
            context_parts.append(
                f"[片段{i}] (相关度: {score:.2f})\n{chunk_text}"
            )

        # 用双换行符分隔各片段
        return "\n\n".join(context_parts)

    def _get_chat_history(self, session_id: str) -> list:
        """
        获取指定会话的对话历史

        使用 LangChain 的 RedisChatMessageHistory 管理对话记录
        自动从 Redis 加载历史消息列表

        Args:
            session_id: 会话 ID

        Returns:
            list: LangChain 消息对象列表
        """
        # 使用 Redis 作为对话历史的后端存储
        # key 格式为 chat_history:{session_id}
        message_history = RedisChatMessageHistory(
            session_id=session_id,
            url=f"redis://{settings.REDIS_HOST}:{settings.REDIS_PORT}/{settings.REDIS_DB}",
        )

        # 返回历史消息列表（首次对话时为空列表）
        return message_history.messages

    def _extract_sources(self, chunks: list[dict[str, Any]]) -> list[str]:
        """
        从召回的攻略片段中提取来源信息

        对攻略片段进行去重处理，返回唯一的文本片段列表

        Args:
            chunks: 召回的攻略片段列表

        Returns:
            list[str]: 去重后的攻略片段文本列表
        """
        sources: list[str] = []
        seen_texts: set[str] = set()

        for chunk in chunks:
            chunk_text = chunk.get("chunk_text", "")
            # 截取前 100 个字符作为摘要展示
            summary = chunk_text[:100] + "..." if len(chunk_text) > 100 else chunk_text

            # 去重：避免相同的片段重复出现在来源列表中
            if chunk_text not in seen_texts:
                seen_texts.add(chunk_text)
                sources.append(summary)

        return sources
