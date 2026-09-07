"""
Embedding 向量化服务模块
使用 sentence-transformers 加载中文 Embedding 模型
将文本转换为高维向量，供 Milvus 向量数据库存储和检索
"""

import logging
from typing import Union

import numpy as np
from sentence_transformers import SentenceTransformer

from config.settings import settings

logger = logging.getLogger(__name__)


class EmbeddingService:
    """
    Embedding 向量化服务
    负责将中文文本编码为稠密向量表示
    """

    def __init__(self) -> None:
        """
        初始化 Embedding 服务
        从配置中读取模型名称，加载预训练的 SentenceTransformer 模型
        """
        # 从配置中获取 Embedding 模型名称
        model_name = settings.EMBEDDING_MODEL_NAME
        logger.info(f"正在加载 Embedding 模型: {model_name}")

        try:
            # 加载预训练的 SentenceTransformer 模型
            # 首次加载会从 HuggingFace 下载模型权重，后续会使用本地缓存
            self.model = SentenceTransformer(model_name)
            # 获取模型输出的向量维度，用于 Milvus 集合创建
            self.dimension = self.model.get_sentence_embedding_dimension()
            logger.info(f"Embedding 模型加载成功，向量维度: {self.dimension}")
        except Exception as e:
            # 模型加载失败时记录错误并抛出异常，阻止服务启动
            logger.error(f"Embedding 模型加载失败: {e}")
            raise RuntimeError(f"无法加载 Embedding 模型 '{model_name}': {e}") from e

    def encode(self, texts: list[str]) -> np.ndarray:
        """
        批量将文本编码为向量矩阵

        Args:
            texts: 待编码的文本列表

        Returns:
            np.ndarray: 形状为 (len(texts), dimension) 的向量矩阵
        """
        # 使用模型对文本列表进行批量编码
        # encode 方法会自动处理分词、池化等操作，输出归一化的向量
        embeddings = self.model.encode(
            texts,
            # 显示进度条（仅在文本数量较多时）
            show_progress_bar=len(texts) > 100,
            # 对输出向量进行 L2 归一化，便于后续使用余弦相似度计算
            normalize_embeddings=True,
        )
        return embeddings

    def encode_single(self, text: str) -> list[float]:
        """
        将单条文本编码为向量列表

        Args:
            text: 待编码的单条文本

        Returns:
            list[float]: 一维浮点数列表，长度为模型向量维度
        """
        # 调用批量编码方法处理单条文本，取第一行结果
        # 使用 list() 转换确保返回标准 Python 列表，便于 JSON 序列化
        embedding = self.encode([text])[0]
        return embedding.tolist()
