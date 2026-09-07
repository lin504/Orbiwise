# Orbiwise - 在线旅游平台

高可用在线旅游服务平台，涵盖景点浏览、智能推荐、门票预订、攻略社区核心业务，AI 赋能提供旅游问答、个性化推荐。

## 项目架构

```
Orbiwise/
├── Orbiwise-java/          # Java SpringBoot3 主业务服务（端口 8080）
├── Orbiwise-python/        # Python FastAPI AI RAG 服务（端口 8000）
├── sql/                    # 数据库初始化脚本
└── README.md
```

- **Java 业务服务**：景点/门票/订单/攻略/评论/权限/推荐，通过 HTTP 调用 Python AI 服务
- **Python AI 服务**：RAG 旅游问答（LangChain + Milvus）、协同过滤推荐

## 技术栈

| 层级 | Java 主业务服务 | Python AI 服务 |
|------|----------------|---------------|
| 框架 | Spring Boot 3.2.0 | FastAPI |
| ORM | MyBatis-Plus 3.5.5 | - |
| 数据库 | MySQL 8.0 | - |
| 缓存 | Redis | Redis（会话存储） |
| 向量库 | - | Milvus |
| AI | - | LangChain + OpenAI |
| Embedding | - | sentence-transformers |
| 鉴权 | JWT + RBAC | - |
| 文档 | SpringDoc OpenAPI | FastAPI Swagger |

## 环境要求

- JDK 17+
- Maven 3.8+
- Python 3.10+
- MySQL 8.0+
- Redis 6.0+
- Milvus 2.3+（仅 Python AI 服务需要）

## 快速启动

### 1. 初始化数据库

```bash
# 登录 MySQL 执行建表脚本
mysql -u root -p < sql/init.sql
```

脚本会创建 `orbiwise` 数据库和 16 张核心业务表，并插入默认角色、权限和示例敏感词数据。

### 2. 启动 Java 业务服务（先启动）

```bash
cd Orbiwise-java

# 修改数据库和 Redis 连接配置（如需要）
# 编辑 src/main/resources/application.yml

# 编译并启动
mvn spring-boot:run
```

服务启动后访问：
- API 地址：http://localhost:8080
- Swagger 文档：http://localhost:8080/swagger-ui.html

### 3. 启动 Python AI 服务

```bash
cd Orbiwise-python

# 创建虚拟环境
python -m venv .venv
.venv\Scripts\activate    # Windows
# source .venv/bin/activate  # Linux/Mac

# 安装依赖
pip install -r requirements.txt

# 复制环境变量配置并修改
cp .env.example .env
# 编辑 .env，填入 LLM API Key 等配置

# 启动服务
python main.py
```

服务启动后访问：
- API 地址：http://localhost:8000
- Swagger 文档：http://localhost:8000/docs
- 健康检查：http://localhost:8000/health

## 数据库表结构（16 张表）

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| role | 角色表 |
| permission | 权限表 |
| user_role | 用户-角色关联表 |
| role_perm | 角色-权限关联表 |
| scenic | 景点表 |
| ticket | 门票表 |
| travel_order | 订单表 |
| strategy | 攻略表 |
| comment | 评论表 |
| user_behavior | 用户行为表 |
| sensitive_word | 敏感词表 |
| rag_doc | RAG 原始文档表 |
| rag_chunk | RAG 分片表 |
| ai_session | AI 会话表 |
| recommend_log | 推荐日志表 |

## 接口文档

### Java 业务接口（端口 8080）

#### 认证接口

```bash
# 用户登录，获取 JWT Token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123456"}'

# 用户注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "123456", "nickname": "旅行者"}'
```

#### 景点接口

```bash
# 景点列表（分页 + 分类筛选 + 关键词搜索）
curl -X GET "http://localhost:8080/api/scenic/list?page=1&size=10&category=自然风光&keyword=西湖" \
  -H "Authorization: Bearer {token}"

# 景点详情
curl -X GET http://localhost:8080/api/scenic/1 \
  -H "Authorization: Bearer {token}"

# 新增景点（需权限 scenic:write）
curl -X POST http://localhost:8080/api/scenic \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "西湖风景区",
    "description": "杭州西湖，世界文化遗产...",
    "address": "浙江省杭州市西湖区",
    "category": "自然风光",
    "level": "AAAAA"
  }'
```

#### 门票接口

```bash
# 查询景点门票
curl -X GET http://localhost:8080/api/ticket/scenic/1 \
  -H "Authorization: Bearer {token}"
```

#### 订单接口

```bash
# 创建订单
curl -X POST http://localhost:8080/api/order \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketId": 1,
    "quantity": 2,
    "contactName": "张三",
    "contactPhone": "13800138000",
    "visitDate": "2025-01-01"
  }'

# 我的订单列表
curl -X GET "http://localhost:8080/api/order/list?page=1&size=10" \
  -H "Authorization: Bearer {token}"

# 取消订单
curl -X PUT http://localhost:8080/api/order/1/cancel \
  -H "Authorization: Bearer {token}"

# 支付订单（模拟）
curl -X PUT http://localhost:8080/api/order/1/pay \
  -H "Authorization: Bearer {token}"
```

#### 攻略接口

```bash
# 攻略列表
curl -X GET "http://localhost:8080/api/strategy/list?page=1&size=10" \
  -H "Authorization: Bearer {token}"

# 发布攻略
curl -X POST http://localhost:8080/api/strategy \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "杭州三日游攻略",
    "content": "第一天游览西湖...",
    "scenicId": 1,
    "tags": "杭州,西湖,三日游"
  }'

# 点赞攻略
curl -X PUT http://localhost:8080/api/strategy/1/like \
  -H "Authorization: Bearer {token}"
```

#### 评论接口（含敏感词校验）

```bash
# 发布评论（内部自动调用 Trie 敏感词过滤器校验）
curl -X POST http://localhost:8080/api/comment \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "scenicId": 1,
    "content": "风景非常美，值得一去！",
    "rating": 5
  }'

# 景点评论列表
curl -X GET "http://localhost:8080/api/comment/scenic/1?page=1&size=10" \
  -H "Authorization: Bearer {token}"
```

#### 推荐接口

```bash
# 获取个性化推荐（协同过滤算法）
curl -X GET "http://localhost:8080/api/recommend/list?topN=10" \
  -H "Authorization: Bearer {token}"
```

#### 权限管理接口

```bash
# 查询用户权限
curl -X GET http://localhost:8080/api/permission/user/1 \
  -H "Authorization: Bearer {token}"

# 分配角色
curl -X POST http://localhost:8080/api/permission/role/assign \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "roleId": 2}'
```

#### AI 助手接口（Java 代理调用 Python）

```bash
# 文档入库
curl -X POST http://localhost:8080/api/ai/rag/store \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "杭州西湖旅游攻略",
    "content": "西湖位于浙江省杭州市西湖区，面积约6.39平方千米...",
    "source": "平台攻略库",
    "docType": "strategy"
  }'

# AI 问答
curl -X POST http://localhost:8080/api/ai/rag/chat \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "西湖有什么好玩的？",
    "sessionId": "session-001"
  }'

# 清空会话
curl -X POST http://localhost:8080/api/ai/rag/clear \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "session-001"}'
```

### Python AI 接口（端口 8000）

```bash
# 文档入库
curl -X POST http://localhost:8000/api/rag/store \
  -H "Content-Type: application/json" \
  -d '{
    "title": "黄山旅游攻略",
    "content": "黄山位于安徽省南部，是中国最著名的山岳风景区之一...",
    "source": "旅游百科",
    "docType": "strategy"
  }'

# RAG 问答
curl -X POST http://localhost:8000/api/rag/chat \
  -H "Content-Type: application/json" \
  -d '{
    "question": "黄山有哪些必看的景点？",
    "sessionId": "session-001"
  }'

# 清空会话
curl -X POST http://localhost:8000/api/rag/clear_session \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "session-001"}'

# 协同过滤推荐
curl -X POST http://localhost:8000/api/recommend/collaborative \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "1",
    "behaviors": [
      {"target_id": "1", "behavior_type": "view", "score": 1},
      {"target_id": "2", "behavior_type": "collect", "score": 3}
    ],
    "top_n": 5
  }'

# 健康检查
curl http://localhost:8000/health
```

## 核心模块说明

### RAG AI 旅游助手
基于检索增强生成架构，解决大模型幻觉问题。攻略文本经分块、Embedding 向量化后存入 Milvus，用户提问时召回真实攻略片段作为上下文送入大模型，返回准确回答。LangChain 维护多轮对话上下文记忆。

### 协同过滤推荐引擎
基于用户行为数据（浏览/收藏/下单），构建用户-景点评分矩阵，使用余弦相似度计算用户间相似性，找到 K 近邻用户，基于相似用户偏好预测推荐分数。

### Trie 树敏感词过滤
基于字典树实现毫秒级敏感词检测，支持后台动态加载词典（无需重启），在评论发布时自动校验拦截违规内容。

### JWT + RBAC 权限体系
用户-角色-权限三级模型，JWT 无状态 Token 认证，自定义 `@RequirePermission` 注解 + AOP 切面实现接口级权限控制。
