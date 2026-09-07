"""
Milvus 向量存储服务模块
负责与 Milvus 向量数据库的交互操作
包括集合创建、向量插入、相似度搜索和向量删除
"""

import logging
from typing import Any

import numpy as np
from pymilvus import (
    Collection,
    CollectionSchema,
    DataType,
    FieldSchema,
    connections,
    utility,
)

from config.settings import settings

logger = logging.getLogger(__name__)


class VectorStoreService:
    """
    Milvus 向量存储服务
    管理向量集合的创建、插入、检索和删除操作
    """

    def __init__(self, embedding_dimension: int) -> None:
        """
        初始化向量存储服务

        Args:
            embedding_dimension: Embedding 模型的向量维度，用于创建集合时指定
        """
        # 保存向量维度，后续创建集合时需要使用
        self.dimension = embedding_dimension
        # Milvus 集合名称，从配置中读取
        self.collection_name = settings.MILVUS_COLLECTION_NAME

        try:
            # 连接 Milvus 服务
            # 使用配置中的 host 和 port 建立 gRPC 连接
            connections.connect(
                alias="default",
                host=settings.MILVUS_HOST,
                port=settings.MILVUS_PORT,
            )
            logger.info(
                f"已连接 Milvus 服务: {settings.MILVUS_HOST}:{settings.MILVUS_PORT}"
            )

            # 检查集合是否已存在，存在则复用，不存在则创建
            if utility.has_collection(self.collection_name):
                # 集合已存在，直接获取引用
                self.collection = Collection(self.collection_name)
                logger.info(f"复用已有 Milvus 集合: {self.collection_name}")
            else:
                # 集合不存在，创建新的集合
                self.collection = self._create_collection()
                logger.info(f"已创建新的 Milvus 集合: {self.collection_name}")

        except Exception as e:
            # 连接或创建集合失败时记录错误并抛出异常
            logger.error(f"Milvus 连接/初始化失败: {e}")
            raise RuntimeError(f"无法初始化 Milvus 向量存储服务: {e}") from e

    def _create_collection(self) -> Collection:
        """
        创建 Milvus 向量集合

        集合包含以下字段：
        - id: 自增主键
        - embedding: 浮点向量字段，维度由 Embedding 模型决定
        - doc_id: 文档唯一标识（字符串类型）
        - chunk_text: 分块后的文本内容
        - chunk_index: 分块在原文中的序号

        Returns:
            Collection: 创建好的 Milvus 集合对象
        """
        # 定义集合的字段结构
        fields = [
            # 自增主键字段，Milvus 自动生成唯一 ID
            FieldSchema(
                name="id",
                dtype=DataType.INT64,
                is_primary=True,
                auto_id=True,
                description="自增主键",
            ),
            # 浮点向量字段，存储文本的 Embedding 向量
            # 维度必须与 Embedding 模型输出一致
            FieldSchema(
                name="embedding",
                dtype=DataType.FLOAT_VECTOR,
                dim=self.dimension,
                description="文本 Embedding 向量",
            ),
            # 文档 ID 字段，标识向量所属的原始文档
            FieldSchema(
                name="doc_id",
                dtype=DataType.VARCHAR,
                max_length=128,
                description="文档唯一标识",
            ),
            # 文本内容字段，存储分块后的原始文本
            FieldSchema(
                name="chunk_text",
                dtype=DataType.VARCHAR,
                max_length=4096,
                description="分块后的文本内容",
            ),
            # 分块序号字段，记录该块在原文中的位置
            FieldSchema(
                name="chunk_index",
                dtype=DataType.INT32,
                description="分块在原文中的序号",
            ),
        ]

        # 创建集合结构定义
        schema = CollectionSchema(
            fields=fields,
            description="旅游 RAG 知识库向量集合",
        )

        # 在 Milvus 中创建集合
        collection = Collection(name=self.collection_name, schema=schema)

        # 创建 IVF_FLAT 索引，使用余弦相似度度量
        # IVF_FLAT 适合中等规模数据，兼顾精度和性能
        index_params = {
            # 索引类型：倒排文件索引 + 平面搜索
            "index_type": "IVF_FLAT",
            # 度量方式：余弦相似度，适合归一化后的文本向量
            "metric_type": "COSINE",
            # 聚类中心数量，影响索引精度和构建速度
            "params": {"nlist": 128},
        }

        # 为 embedding 字段创建索引
        collection.create_index(
            field_name="embedding",
            index_params=index_params,
        )

        logger.info("Milvus 集合创建完成，索引类型: IVF_FLAT, 度量: COSINE")
        return collection

    def insert_vectors(
        self,
        embeddings: np.ndarray,
        doc_ids: list[str],
        chunk_texts: list[str],
        chunk_indices: list[int],
    ) -> None:
        """
        批量插入向量数据到 Milvus

        Args:
            embeddings: 向量矩阵，形状为 (n, dimension)
            doc_ids: 每个向量对应的文档 ID 列表
            chunk_texts: 每个向量对应的文本内容列表
            chunk_indices: 每个向量对应的分块序号列表
        """
        # 将 numpy 数组转换为 list of list，Milvus 要求此格式
        embedding_list = embeddings.tolist()

        # 组装插入数据，字段顺序必须与集合 schema 一致
        data = [
            embedding_list,  # embedding 字段
            doc_ids,         # doc_id 字段
            chunk_texts,     # chunk_text 字段
            chunk_indices,   # chunk_index 字段
        ]

        # 执行批量插入操作
        self.collection.insert(data)

        # 插入后立即刷新集合，确保数据可被搜索到
        self.collection.flush()

        logger.info(f"成功插入 {len(embeddings)} 条向量数据到集合 {self.collection_name}")

    def search_vectors(
        self,
        query_embedding: list[float],
        top_k: int = 5,
    ) -> list[dict[str, Any]]:
        """
        相似度搜索，返回与查询向量最相关的 top_k 个片段

        Args:
            query_embedding: 查询文本的 Embedding 向量
            top_k: 返回的最相似结果数量

        Returns:
            list[dict]: 搜索结果列表，每个元素包含 doc_id、chunk_text、chunk_index 和相似度分数
        """
        # 加载集合到内存，确保搜索性能
        self.collection.load()

        # 搜索参数配置
        search_params = {
            # 搜索时检查的聚类中心数量，越大越精确但越慢
            "params": {"nprobe": 16},
        }

        # 执行向量相似度搜索
        results = self.collection.search(
            # 查询向量，需要包装为二维列表
            data=[query_embedding],
            # 搜索的向量字段
            anns_field="embedding",
            # 搜索参数
            param=search_params,
            # 返回结果数量
            limit=top_k,
            # 指定返回的标量字段
            output_fields=["doc_id", "chunk_text", "chunk_index"],
        )

        # 解析搜索结果，转换为字典列表
        search_results: list[dict[str, Any]] = []
        for hit in results[0]:
            search_results.append({
                # 文档 ID
                "doc_id": hit.entity.get("doc_id"),
                # 分块文本内容
                "chunk_text": hit.entity.get("chunk_text"),
                # 分块序号
                "chunk_index": hit.entity.get("chunk_index"),
                # 相似度分数（余弦相似度，值域 [0, 1]）
                "score": hit.score,
            })

        logger.info(f"向量搜索完成，返回 {len(search_results)} 条结果")
        return search_results

    def delete_by_doc_id(self, doc_id: str) -> None:
        """
        删除指定文档的所有向量

        根据 doc_id 字段过滤，删除该文档的所有分块向量

        Args:
            doc_id: 要删除的文档唯一标识
        """
        # 构建删除表达式，匹配指定 doc_id 的所有记录
        expr = f'doc_id == "{doc_id}"'

        # 执行删除操作
        self.collection.delete(expr)

        # 删除后刷新集合，确保变更立即生效
        self.collection.flush()

        logger.info(f"已删除文档 {doc_id} 的所有向量数据")

    def is_connected(self) -> bool:
        """
        检查 Milvus 连接是否正常

        Returns:
            bool: 连接正常返回 True，否则返回 False
        """
        try:
            # 尝试列出集合来验证连接状态
            utility.has_collection(self.collection_name)
            return True
        except Exception:
            return False
