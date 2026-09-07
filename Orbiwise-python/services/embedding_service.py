"""
Embedding 向量化服务模块
使用阿里云 DashScope 文本向量 API（OpenAI 兼容接口）
将文本转换为高维向量，供 Milvus 向量数据库存储和检索
"""

import logging
from typing import Union

import numpy as np
from langchain_openai import OpenAIEmbeddings

from config.settings import settings

logger = logging.getLogger(__name__)


class EmbeddingService:
    """
    Embedding 向量化服务
    通过阿里云 DashScope OpenAI 兼容接口调用文本向量模型
    无需本地加载模型，直接通过 API 获取向量结果
    """

    def __init__(self) -> None:
        """
        初始化 Embedding 服务
        使用阿里云 DashScope 的 OpenAI 兼容接口创建 Embedding 客户端
        """
        # 从配置中获取 Embedding 模型名称
        model_name = settings.EMBEDDING_MODEL_NAME
        logger.info(f"正在初始化 DashScope Embedding 服务，模型: {model_name}")

        try:
            # 创建 LangChain OpenAIEmbeddings 客户端
            # 通过 DashScope 的 OpenAI 兼容接口调用阿里云文本向量模型
            # 复用千问 LLM 的 API Key 和接口地址，无需额外配置
            self.client = OpenAIEmbeddings(
                model=model_name,
                openai_api_key=settings.ALIYUN_API_KEY,
                openai_api_base=settings.LLM_API_BASE,
            )

            # 通过一次测试调用获取向量维度
            # 不同模型维度不同：v1=1536, v2=1536, v3=1024（默认）
            test_embedding = self.client.embed_query("维度测试")
            self.dimension = len(test_embedding)

            logger.info(f"DashScope Embedding 服务初始化成功，向量维度: {self.dimension}")

        except Exception as e:
            # 初始化失败时记录错误并抛出异常，阻止服务启动
            logger.error(f"DashScope Embedding 服务初始化失败: {e}")
            raise RuntimeError(
                f"无法初始化 DashScope Embedding 模型 '{model_name}'，"
                f"请检查 ALIYUN_API_KEY 是否正确: {e}"
            ) from e

    def encode(self, texts: list[str]) -> np.ndarray:
        """
        批量将文本编码为向量矩阵

        通过 DashScope API 批量获取向量，比逐条调用效率更高

        Args:
            texts: 待编码的文本列表

        Returns:
            np.ndarray: 形状为 (len(texts), dimension) 的向量矩阵
        """
        # 使用 LangChain 的 embed_documents 方法批量获取向量
        # DashScope 兼容接口支持批量输入，单次请求返回所有向量
        embeddings = self.client.embed_documents(texts)

        # 转换为 numpy 数组，形状为 (len(texts), dimension)
        return np.array(embeddings, dtype=np.float32)

    def encode_single(self, text: str) -> list[float]:
        """
        将单条文本编码为向量列表

        使用 embed_query 方法，适用于查询场景（如 RAG 检索时的用户问题）

        Args:
            text: 待编码的单条文本

        Returns:
            list[float]: 一维浮点数列表，长度为模型向量维度
        """
        # 使用 embed_query 方法编码单条查询文本
        # 与 embed_documents 的区别在于，query 端会做查询特定的归一化处理
        embedding = self.client.embed_query(text)
        return embedding
