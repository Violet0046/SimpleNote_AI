# 📕 SimpleNote AI - 全栈社交图文/视频分享平台

SimpleNote AI 是一款内容社区全栈社交应用。本项目不仅实现了完整的图文/视频发布、瀑布流发现、关注与互动体系，更在架构层面进行了极具前瞻性的探索：前端采用企业级的 **FSD (Feature-Sliced Design) 架构**，后端采用 **Java (核心业务) + Python (AI 智能生成) 异构微服务** 协同方案。

## ✨ 核心功能亮点

### 📱 沉浸式交互体验
* **高性能响应式瀑布流**：发现页基于 `IntersectionObserver` 实现无缝触底加载。
* **仿大厂风格路由体验**：刷流时新开标签页看主页、弹窗式沉浸式帖子详情。

### 🎨 专业创作者中心
* **极简发布流程**：支持一次性上传最多 18 张图片或 1 个高清视频。
* **多级评论树架构**：支持顶层评论与子评论、楼主/作者专属标识、点赞互动。

### 🤖 AI Agent 智能服务 (Python 微服务)
* **独立微服务解耦**：不阻塞 Java 核心主线程，保障高并发下的系统稳定性。
* **多模态内容生成**：结合用户上传图片与短关键词，智能生成种草文案、表情包与热门 Tags。

## 🏗️ 架构设计

### 前端：FSD (Feature-Sliced Design)
摒弃传统的 MVC 按文件类型堆砌，采用底层特性切片设计，完美解决大型 Vue 3 项目的代码耦合痛点：
* `app/`：应用全局配置 (Router, 顶级 Providers)。
* `pages/`：极薄的视图容器，负责模块组装。
* `modules/`：高内聚业务模块 (如 `post`, `social`, `auth`)，内聚自身的 API、Types、Stores 和 UI。
* `shared/`：绝对底层的公共 UI 积木、HTTP 网络封装与通用 Composables (`useInfiniteScroll`, `useMasonryColumns`)。

### 后端：Java + Python 异构微服务
* **Java 后端 (Spring Boot)**：负责所有高频、高可用要求的核心业务（CRUD、鉴权、关系链、点赞）。
* **Python 后端 (FastAPI)**：通过 CORS 配置被前端直接调用，利用 Python 生态优势接入大模型，提供 AI 文本生成服务。

## 🛠️ 环境要求

* **Node.js** >= 18
* **JDK** 17
* **Maven** 3.6+
* **Python** 3.10+
* **MySQL** 8.0

## 🚀 快速启动

### 1. 数据库初始化
1. 创建名为 `simplenote` 的 MySQL 数据库。
2. 运行项目根目录下的 `init.sql` 脚本完成建表。

### 2. Java 后端 (核心服务)
```bash
cd note-java-backend/backend

mvn clean install
.\mvnw spring-boot:run
```
**提示：项目已配置默认连接本地 MySQL (root / 1234，数据库名 simplenote_db)。**<br>
**若你的本地数据库配置不同，或连接云数据库，无需修改代码，请直接配置环境变量：DB_HOST=你的IP DB_PORT=3306 DB_NAME=库名 DB_USERNAME=账号 DB_PASSWORD=密码**<br>
**服务将运行在 http://localhost:8080**
### 3. Python 后端 (AI Agent 微服务)
**配置你的 OpenAI API Key (如果在 .env 中)**
```bash
cd ai-agent-service
pip install -r requirements.txt
python app.py
```
**服务将运行在 http://localhost:8000**

### 4. 前端启动
```bash
cd note-frontend
npm install
npm run dev
```
**访问 http://localhost:5173**

## 项目目录速览
```text
SimpleNote_AI/
├── note-frontend/              # 前端 Vue 3 工程 (FSD架构)
│   ├── src/modules/            # 核心业务域 (auth, post, profile, social, ai)
│   ├── src/shared/             # 底层公共组件与 Hooks
│   └── src/pages/              # 页面级容器
├── note-java-backend/          # Java 核心后端
│   └── backend/src/main/java/  # Spring Boot (Controller, Service, Mapper, Pojo)
├── ai-agent-service/           # Python AI 微服务
│   └── src/api/routes/         # FastAPI 路由逻辑
└── init.sql                    # 数据库初始化脚本
```

docker exec -it simplenote-redis redis-cli
# 
KEYS post:*
"post:likes:init:3"      帖子 3 的点赞集合，已经从 MySQL 加载到 Redis 里了
"post:likes:users:3"     真正的点赞关系数据

SMEMBERS post:likes:users:3   哪些用户给帖子 3 点过赞

# GET post:likes:init:3
如果返回 1，就表示后端已经做过这一步：
去 MySQL 查帖子 3 的点赞用户
把这些用户装进 Redis
然后打上这个初始化标记