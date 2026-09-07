"""
FastAPI AI RAG 旅游助手服务入口
在线旅游平台的 AI 助手后端，使用 RAG 架构提供旅游问答服务
包含 RAG 智能问答和协同过滤推荐两大核心功能
"""

import logging
import os
import sys
from contextlib import asynccontextmanager

import redis
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from config.settings import settings
from models.schemas import HealthResponse
from routers import rag_router, recommend_router
from services.embedding_service import EmbeddingService
from services.rag_service import RagService
from services.recommend_service import RecommendService
from services.vector_store_service import VectorStoreService

# 设置 Hugging Face 镜像源（国内网络无法直接访问 huggingface.co）
# 必须在 Embedding 模型加载之前设置，huggingface_hub 会自动使用该端点下载模型
os.environ["HF_ENDPOINT"] = settings.HF_ENDPOINT

# 配置日志格式和级别
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    handlers=[logging.StreamHandler(sys.stdout)],
)
logger = logging.getLogger(__name__)

# 全局服务实例引用，供健康检查等接口使用
_embedding_service: EmbeddingService | None = None
_vector_store_service: VectorStoreService | None = None
_rag_service: RagService | None = None
_recommend_service: RecommendService | None = None
_redis_client: redis.Redis | None = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    应用生命周期管理
    startup: 初始化所有服务（Embedding 模型、Milvus 连接、Redis 连接）
    shutdown: 关闭所有外部连接
    """
    global _embedding_service, _vector_store_service, _rag_service
    global _recommend_service, _redis_client

    logger.info("正在启动 AI 旅游助手服务...")

    # ==================== Startup: 初始化各服务 ====================

    try:
        # 第一步：初始化 Embedding 服务
        # 加载中文 Embedding 模型（首次加载可能需要下载模型文件）
        logger.info("正在初始化 Embedding 服务...")
        _embedding_service = EmbeddingService()

        # 第二步：初始化 Milvus 向量存储服务
        # 连接 Milvus 并创建/获取向量集合
        logger.info("正在初始化 Milvus 向量存储服务...")
        _vector_store_service = VectorStoreService(
            embedding_dimension=_embedding_service.dimension,
        )

        # 第三步：初始化 Redis 连接
        # 用于存储 RAG 对话历史
        logger.info("正在连接 Redis...")
        _redis_client = redis.Redis(
            host=settings.REDIS_HOST,
            port=settings.REDIS_PORT,
            db=settings.REDIS_DB,
            decode_responses=True,
        )
        # 验证 Redis 连接
        _redis_client.ping()

        # 第四步：初始化 RAG 核心服务
        # 整合 Embedding、向量存储和 LLM 对话能力
        logger.info("正在初始化 RAG 核心服务...")
        _rag_service = RagService(
            embedding_service=_embedding_service,
            vector_store_service=_vector_store_service,
        )

        # 将 RAG 服务注入到路由模块
        rag_router.set_rag_service(_rag_service)

        # 第五步：初始化推荐服务
        logger.info("正在初始化推荐服务...")
        _recommend_service = RecommendService()
        recommend_router.set_recommend_service(_recommend_service)

        logger.info("AI 旅游助手服务启动完成！")

    except Exception as e:
        logger.error(f"服务启动失败: {e}")
        raise

    # 应用运行中...
    yield

    # ==================== Shutdown: 关闭连接 ====================
    logger.info("正在关闭 AI 旅游助手服务...")

    # 关闭 Redis 连接
    if _redis_client:
        _redis_client.close()
        logger.info("Redis 连接已关闭")

    logger.info("AI 旅游助手服务已关闭")


# ==================== 创建 FastAPI 应用 ====================
app = FastAPI(
    title="AI 旅游助手 RAG 服务",
    description=(
        "在线旅游平台 AI 助手后端服务，基于 RAG（检索增强生成）架构，"
        "提供旅游攻略智能问答和协同过滤推荐功能。"
    ),
    version="1.0.0",
    lifespan=lifespan,
)

# ==================== 中间件配置 ====================

# CORS 跨域中间件配置
# 允许前端和其他服务跨域访问 API
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],       # 允许所有来源（生产环境应限制具体域名）
    allow_credentials=True,    # 允许携带凭证
    allow_methods=["*"],       # 允许所有 HTTP 方法
    allow_headers=["*"],       # 允许所有请求头
)


# ==================== 全局异常处理中间件 ====================
@app.middleware("http")
async def global_exception_handler(request: Request, call_next):
    """
    全局异常处理中间件
    捕获所有未处理的异常，返回统一的错误响应格式
    """
    try:
        response = await call_next(request)
        return response
    except Exception as e:
        logger.error(f"未处理的异常: {e}", exc_info=True)
        return JSONResponse(
            status_code=500,
            content={"detail": f"服务器内部错误: {str(e)}"},
        )


# ==================== 路由注册 ====================
app.include_router(rag_router.router)
app.include_router(recommend_router.router)


# ==================== 健康检查接口 ====================
@app.get("/health", response_model=HealthResponse, tags=["系统"])
async def health_check() -> HealthResponse:
    """
    健康检查接口

    检查服务本身及依赖组件（Milvus、Redis）的连接状态，
    用于运维监控和负载均衡器的存活探测。

    返回:
        HealthResponse: 健康状态信息，包含：
            - status: 服务状态
            - milvus_connected: Milvus 连接状态
            - redis_connected: Redis 连接状态
    """
    # 检查 Milvus 连接状态
    milvus_ok = False
    if _vector_store_service:
        milvus_ok = _vector_store_service.is_connected()

    # 检查 Redis 连接状态
    redis_ok = False
    if _redis_client:
        try:
            _redis_client.ping()
            redis_ok = True
        except Exception:
            redis_ok = False

    # 综合判断服务健康状态
    status = "healthy" if (milvus_ok and redis_ok) else "degraded"

    return HealthResponse(
        status=status,
        milvus_connected=milvus_ok,
        redis_connected=redis_ok,
    )


# ==================== 启动入口 ====================
if __name__ == "__main__":
    import uvicorn

    # 使用 uvicorn 启动服务
    # host 和 port 从配置文件读取
    uvicorn.run(
        "main:app",
        host=settings.SERVER_HOST,
        port=settings.SERVER_PORT,
        reload=True,  # 开发模式：文件变更自动重启
    )
