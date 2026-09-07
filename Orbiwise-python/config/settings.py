"""
配置管理模块
使用 pydantic-settings 从 .env 文件中读取所有配置项
集中管理 Milvus、Redis、LLM、Embedding 以及服务运行参数
"""

from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    """
    全局配置类
    从项目根目录的 .env 文件中自动加载环境变量
    """

    # ==================== Milvus 向量数据库配置 ====================
    # Milvus 服务地址
    MILVUS_HOST: str = "localhost"
    # Milvus 服务端口
    MILVUS_PORT: int = 19530

    # ==================== Redis 配置（用于会话缓存） ====================
    # Redis 服务地址
    REDIS_HOST: str = "localhost"
    # Redis 服务端口
    REDIS_PORT: int = 6379
    # Redis 数据库编号（默认使用 DB 1 与业务数据隔离）
    REDIS_DB: int = 1

    # ==================== 大模型 API 配置（OpenAI 兼容接口） ====================
    # LLM API 基础地址（支持 OpenAI 兼容的第三方接口）
    LLM_API_BASE: str = "https://api.openai.com/v1"
    # LLM API 密钥
    LLM_API_KEY: str = "your-api-key-here"
    # 使用的模型名称
    LLM_MODEL_NAME: str = "gpt-3.5-turbo"

    # ==================== Embedding 模型配置 ====================
    # 中文 Embedding 模型名称（基于 sentence-transformers 加载）
    EMBEDDING_MODEL_NAME: str = "shibing624/text2vec-base-chinese"

    # ==================== Milvus 集合配置 ====================
    # 向量存储集合名称
    MILVUS_COLLECTION_NAME: str = "travel_rag"

    # ==================== 服务配置 ====================
    # 服务监听地址
    SERVER_HOST: str = "0.0.0.0"
    # 服务监听端口
    SERVER_PORT: int = 8000

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"


# 创建全局配置实例，供其他模块直接引用
settings = Settings()
