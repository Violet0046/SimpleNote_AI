# SimpleNote 项目说明

更新时间：2026-04-22

## 项目定位

SimpleNote 是一个偏小红书风格的内容社区项目，当前已经完成三条核心链路：

- 内容发布：图文 / 视频发帖、上传、详情展示
- 社交互动：点赞、评论、回复、关注、个人主页
- AI 能力：AI 发帖文案生成、站内社交搜推 Agent MVP

这个项目现在不是“一个单纯的 CRUD 社区”，而是一个开始向 AI 原生系统演进的小型内容平台。

## 当前整体架构

项目目前是三段式架构：

- `note-frontend`
  - Vue 3 + TypeScript + Pinia + Vue Router + Element Plus
  - 负责内容流、发布页、个人主页和后续 AI 结果展示
- `note-java-backend`
  - Spring Boot 3 + MyBatis + MySQL + Redis
  - 负责用户、帖子、评论、点赞、关注等核心业务
  - 现在也开始承担“知识环境层”的职责
- `ai-agent-service`
  - FastAPI + OpenAI Compatible 调用方式
  - 负责 AI 内容生成与 Agent 编排

整体链路可以概括成：

```text
前端 / 终端测试
-> Java 后端（业务系统）
-> AI Agent Service（Agent 编排）
-> Java 知识接口（知识环境）
-> LLM
```

### Java 后端当前分层已经重构

这次在 Java 后端里，不只是补了功能，还顺手把目录和职责重新拉直了。当前后端更适合这样理解：

- `web`
  - HTTP 入口层，负责 Controller
- `security`
  - JWT、登录拦截器、用户上下文
- `interaction`
  - 点赞、关注相关的 Redis 状态、Stream、补偿同步
- `ai/knowledge`
  - 给 AI 服务提供知识环境接口
- `config`
  - 当前主要是 Web 配置，后续可扩 Redis / Async
- `service / mapper / pojo`
  - 继续承接通用业务服务、数据访问与对象模型

这一步的价值在于：后续继续往点赞链路、AI 能力、读写分离方向演进时，结构不会越来越乱。

## 技术栈与目录概览

### 技术栈

- 前端：Vue 3 + TypeScript + Pinia + Vue Router + Element Plus
- Java 后端：Spring Boot 3 + MyBatis + MySQL + Redis
- AI 微服务：FastAPI + OpenAI Compatible 接口调用

### 项目目录

```text
SimpleNote_AI/
├── note-frontend/              # 前端工程
├── note-java-backend/          # Java 核心业务后端
├── ai-agent-service/           # Python AI 微服务
├── init.sql                    # 数据库初始化脚本
├── seed_test_posts.sql         # 测试帖子种子数据
├── seed_test_comments.sql      # 测试评论种子数据
└── test_social_agent.py        # Agent 终端测试脚本
```

### 当前更准确的系统定位

这不是一个“已经完整商业化”的内容平台，更准确的说法是：

- 一个已经打通社区主链路的小型内容平台
- 一个已经具备 AI 文案生成能力的异构微服务系统
- 一个开始向 RAG / Agent / 知识环境演进的 AI 原生项目

## 项目当前真实做到的能力与边界

### 已经真实做到的

- 注册、登录、JWT 鉴权
- 用户资料编辑
- 图文帖子与视频帖子发布
- 本地文件上传与 URL 回填
- 首页内容流与帖子详情
- 评论树与回复
- 点赞、取消点赞
- 关注、取关、关注列表与粉丝列表
- 我的主页 / 他人主页
- AI 发帖文案生成
- 社交搜推 Agent MVP

### 当前仍然没有做的

- 真正的全站搜索引擎
- 生产级推荐算法
- 完整的内容审核流
- 通知中心后端真实化
- 直播功能
- 向量数据库与全站 embedding
- 长期记忆 / 个性化记忆
- 完整自动化测试体系

### 为什么要把边界写清楚

因为这份文档既是给自己复习用的，也是给 AI 理解项目用的。  
如果不区分“已经实现”和“计划实现”，后面无论是面试表达还是继续开发，都容易把项目说得过满。

## 架构演化痕迹

这个项目不是一次性设计完的，而是明显经历过几轮演进。

### 1. 社交互动从纯数据库读写演进到了 Redis 协同

点赞和关注现在都不只是“落数据库”，而是开始使用 Redis 承担高频读写与状态读取。

这意味着项目已经从：

- 数据库直读直写

演进到了：

- Redis 维护互动状态
- 定时任务做异步回刷
- 数据库承担持久化

继续往前，这条链路又做了一轮升级：

- 外部接口从 `toggle` 改成了“状态设定型”
- Redis 写入从普通多步命令改成了 Lua 原子更新
- 变更事件会写入 Redis Stream
- 主异步链路开始由 Stream consumer 推动
- dirty set + processing set 作为补偿链继续保留
- pending reclaim、DLQ 和消费日志也已经接入第一版

也就是说，点赞/关注已经不再是“前端点一下，后端自己翻一下状态”的简单 toggle 模式，而是开始向“有主异步链、补偿链和异常兜底”的工程化设计演进。

### 2. AI 服务从单功能服务演进成 feature 化服务

原先 `ai-agent-service` 只有“AI 发帖文案生成”这一个能力。  
这次新增社交搜推 Agent 后，如果继续沿旧方式堆代码，整个 AI 服务会很快变乱。

所以这次改造成了更清晰的 feature 结构：

- `features/content`
- `features/agent`
- `integrations`
- `providers`

### 3. Java 后端开始承担“知识环境层”

Java 后端之前主要是业务系统。  
现在随着 Agent 接入，它除了继续负责业务 CRUD，也开始承担：

- 帖子知识抽取
- 评论摘要组织
- 知识接口输出

这就是从“业务后端”向“业务后端 + 知识环境层”演进。

## 关键代码锚点

下面这些文件是理解整个项目最重要的入口。

### 前端入口与关键页面

- `note-frontend/src/main.ts`
- `note-frontend/src/router/index.ts`
- `note-frontend/src/views/FeedView.vue`
- `note-frontend/src/modules/post/composables/useCreatePostFlow.ts`
- `note-frontend/src/modules/ai/composables/useAiPostAssistant.ts`

### Java 后端关键入口

- `note-java-backend/backend/src/main/java/com/simplenote/backend/BackendApplication.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/web/controller/PostController.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/web/controller/UserController.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/interaction/InteractionRedisService.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/interaction/sync/InteractionSyncScheduler.java`

### 新增知识环境相关锚点

- `note-java-backend/backend/src/main/java/com/simplenote/backend/ai/knowledge/controller/KnowledgePostController.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/ai/knowledge/service/impl/KnowledgePostServiceImpl.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/ai/knowledge/config/AgentKnowledgeProperties.java`
- `note-java-backend/backend/src/main/resources/application.yml`

### 互动链路关键锚点

- `note-java-backend/backend/src/main/java/com/simplenote/backend/interaction/redis/InteractionRedisLikeService.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/interaction/redis/InteractionRedisFollowService.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/interaction/sync/InteractionProjectionService.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/interaction/sync/InteractionSyncService.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/interaction/stream/InteractionStreamConsumer.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/interaction/stream/InteractionStreamReclaimer.java`
- `note-java-backend/backend/src/main/java/com/simplenote/backend/interaction/stream/InteractionStreamDeadLetterService.java`

### AI 微服务关键入口

- `ai-agent-service/app.py`
- `ai-agent-service/src/api/routes/content.py`
- `ai-agent-service/src/api/routes/agent.py`
- `ai-agent-service/src/features/content/service.py`
- `ai-agent-service/src/features/agent/service.py`
- `ai-agent-service/src/integrations/backend_knowledge_client.py`

## 前端、Java、AI 服务三者分别负责什么

### 前端

- 内容流展示
- 发布页交互
- 用户主页与社交页面
- 后续 AI 问答结果展示

### Java 后端

- 账号、帖子、评论、关注、点赞等核心业务
- Redis 互动状态、Lua 原子更新、Stream 消费与补偿同步
- Agent 所需知识文档的组织与输出

### AI 微服务

- 原有 AI 文案生成
- Query 分析
- 检索增强
- 证据构建
- 答案生成

## 已完成的业务能力

### 社区主链路

- 用户注册、登录、资料编辑
- 发布图文帖子与视频帖子
- 首页内容流展示
- 帖子详情
- 评论与楼中楼回复
- 点赞与取消点赞
- 关注与粉丝关系
- 我的主页 / 他人主页

### Redis 相关能力

当前 Redis 不只是“缓存帖子详情”，还承担一部分互动状态的读写：

- 点赞关系缓存
- 关注关系缓存
- 高频计数读模型
- Stream 事件流
- dirty set / processing set 补偿标记
- 定时补偿回刷数据库

这使项目从“数据库直读直写”演进到了“数据库 + Redis 读写协同”。

## 社交互动链路的关键优化

这一部分是最近一轮最重要的工程化升级，也是后面面试时最值得讲的内容之一。

### 为什么要重构点赞和关注

原先点赞和关注的后端实现本质上是 `toggle`：

- 前端读当前状态
- 点击后发一个“不带目标状态”的 toggle 请求
- 后端自己把状态翻转

这种做法的问题很典型：

- 不幂等，网络重试可能把状态翻回去
- 多端并发或状态过期时，容易和用户真实意图不一致
- 前端只能通过 `!isLiked` / `!isFollowing` 猜测最终结果

所以后来把它重构成了“状态设定型”：

- 点赞：`PUT /post/{id}/like`、`DELETE /post/{id}/like`
- 关注：`PUT /follow/{id}`、`DELETE /follow/{id}`
- 评论点赞也已经改成同样的接口语义

这里最重要的不是按钮有没有切换效果，而是**后端拿到的是明确目标状态，而不是模糊的 toggle 意图**。

### 数据模型为什么没有大改

数据库结构没有为了“状态设定型”硬改成 `status=0/1` 表，而是继续使用关系表表示当前状态：

- `user_likes(user_id, post_id)` 表示当前帖子点赞关系
- `follow_user(follower_id, followed_id)` 表示当前关注关系
- `comment_like(user_id, comment_id)` 表示当前评论点赞关系

对点赞/关注这种二元关系来说：

- 关系存在 = 当前状态为 true
- 关系不存在 = 当前状态为 false

这本身就是非常常见、也很干净的当前状态建模方式。

### Redis 为什么要做双向索引

Redis 里没有加一个单独的“当前状态字段”，而是继续用“关系是否存在”表达状态，同时维护双向索引：

- 帖子点赞：
  - `post -> users`
  - `user -> posts`
- 用户关注：
  - `user -> following`
  - `user -> followers`

这样可以分别支撑：

- 某帖子被哪些人点赞、点赞数是多少
- 某用户点赞过哪些帖子
- 某用户关注了谁
- 某用户有哪些粉丝

这里的 Redis 不是“每个登录用户一套表”，而是**全局共享的互动索引层**。

### Lua 原子更新是怎么做的

点赞和关注的 Redis 写入已经改成 Lua 原子更新。

一次真正发生变化时，Lua 会把这几个动作合成一个最小执行单元：

- 检查当前状态和目标状态是否一致
- 如果已经一致，直接返回，不制造副作用
- 如果不一致：
  - 更新 Redis 当前状态
  - 标记 dirty 聚合根
  - 追加一条 Stream 事件

这样做的价值是：

- 避免多步 Redis 命令之间出现中间态
- 保证“改状态 + 发事件 + 打脏标记”要么一起成功，要么一起不做

### 为什么选择 Redis Stream

这里没有直接接 RabbitMQ / Kafka，而是先用 Redis Stream 作为第一版消息通道，原因主要有三点：

- 项目本来就已经依赖 Redis
- “改 Redis 状态”和“发消息”可以放在 Lua 里原子完成
- 在不新增 outbox 表的前提下，直接上外部 MQ 会有双写缝隙

因此当前的设计是：

- Redis 维护当前互动状态
- Redis Stream 维护变更事件流
- MySQL 保存最终持久化关系和部分读模型

### Stream 消费为什么按聚合根去重

当前不是“逐条事件增量改 SQL”，而是**先按聚合根去重，再按 Redis 当前快照投影 MySQL**。

例如：

- 点赞事件按 `postId` 聚合
- 关注事件按 `followerId` 聚合

原因是：

- 最终要落库的是“当前关系状态”
- 不是把每条点赞/取消点赞都逐条回放成 SQL 增量
- 对同一个 `postId` 或 `followerId`，一批消息里最终同步一次当前快照就够了

这一步很适合点赞/关注这种“当前状态型”业务，但不适合订单、支付这种流水/状态机型业务。

### 当前写链路的完整路径

以帖子点赞为例，现在的完整路径是：

```text
前端发 PUT / DELETE
-> 后端拿到 desiredState
-> Lua 原子更新 Redis 当前状态
-> Lua XADD 到 social:interaction:stream
-> Lua SADD 到 dirty set
-> Stream consumer 拉消息
-> 按 postId 去重
-> 按 Redis 当前快照投影 MySQL
-> ACK 成功消息
-> dirty 补偿任务继续兜底
```

关注链路和这个模型基本一致，只是聚合根换成了 `followerId`。

### 当前的补偿机制怎么做

补偿链没有删除，而是从“主同步手段”降级成了“最终一致性修正器”。

当前做法是：

- dirty set 记录哪些聚合根脏了
- processing set 记录哪些聚合根正在补偿处理中
- 定时任务扫描 dirty
- 通过 `move` 把实体从 dirty 转到 processing
- 同步成功后从 processing 清掉
- 失败则回到 dirty，下次继续补偿

这比直接 `SPOP` 安全得多，因为不会在同步失败时把脏标记直接丢掉。

### 当前的可靠性增强做到哪一步

现在这条链路已经补了第一版的消息可靠性：

- 正常消费与 ACK
- pending 消息 reclaim
- 超时未 ACK 消息重新 claim
- delivery 次数超限后进入 `DLQ`
- 消费日志和错误日志

也就是说，当前点赞/关注已经不只是“有 Redis 缓存”，而是具备了：

- 主异步链
- 补偿链
- pending 重试
- DLQ 兜底

### 当前还保留的边界

这条链路现在已经能较稳地支撑帖子点赞和关注，但仍然有几个边界需要诚实说明：

- 评论点赞还没有完全接入同一套 Redis + Stream + 补偿链
- 还没有做版本号 / 水位机制
- 因此 Stream 消费和 dirty 补偿之间仍可能有重复投影，但当前是可接受的
- 一些 Redis 主读接口还没有统一的 MySQL fallback facade

所以这版更适合定义成：**已经具备工程化基础的第一版可靠互动链路**，不是终局版。

### AI 内容生成

`ai-agent-service` 最初只有一个能力：

- 根据用户上传的图片 / 视频帧 / 关键词生成发帖文案

现在这个能力仍然保留，接口继续兼容：

- `POST /api/content/generate`

## 新增能力：社交搜推 Agent MVP

### 为什么要做这块

原来的 AI 微服务只能“看图写文案”，还不具备站内知识能力。
这次新增的目标，是让它具备最小可用的：

- 站内知识读取
- 检索增强
- 证据约束回答

这样当用户问：

- 最近大家都在讨论什么好玩的去处？
- 雨天适合去哪些室内地方？
- 最近杭州适合周末去哪里？

系统不再是自由发挥，而是基于站内帖子给出 grounded answer。

### 这次采用的实现策略

当前版本不是全站搜索，也不是向量数据库版本，而是一个最小可落地 MVP：

- 先把 `24-60` 号帖子作为固定知识池
- Java 后端把这些帖子组织成知识文档
- AI 服务先取知识，再做轻量召回排序
- 只把 `top-k` 证据交给模型总结

这版重点验证的是：

- 知识环境是否打通
- 检索增强链路是否成立
- 模型是否能基于证据回答而不是胡说

## 新架构放在哪里

### Java 后端新增的职责

Java 后端现在除了原有业务能力，还新增了一层“知识环境层”。

新增模块位置：

- `com.simplenote.backend.ai.knowledge`

主要新增内容：

- `knowledge/controller/KnowledgePostController.java`
- `knowledge/service/KnowledgePostService.java`
- `knowledge/service/impl/KnowledgePostServiceImpl.java`
- `knowledge/dto/*`

这层做的事情不是 Agent 推理，而是：

- 从帖子表读取候选帖子
- 统计评论数
- 取代表性评论
- 做文本清洗和截断
- 输出 AI 可消费的知识文档

对外暴露接口：

- `GET /agent/knowledge/posts`

### AI 微服务新增的职责

AI 服务这次按 feature 重新整理了结构，避免新功能和原文案生成逻辑搅在一起。

现在的组织方式大致是：

- `src/features/content`
  - 原来的 AI 发帖文案生成功能
- `src/features/agent`
  - 新增的社交搜推 Agent
- `src/integrations`
  - 与 Java 后端知识接口交互
- `src/providers`
  - 通用模型调用层

新的 Agent 接口：

- `POST /api/agent/social-search`

这样后面继续扩展记忆、RAG、工具调用时，结构不会乱成一个大脚本。

## 社交搜推 Agent 的执行流程

一次请求的完整链路如下：

```text
用户问题
-> AI 服务接收问题
-> Query Analysis
-> 调用 Java 知识接口拉取固定知识池
-> Candidate Retrieval 轻量打分
-> 选出 top-k 帖子
-> Context Builder 组装证据包
-> LLM 基于证据总结答案
-> 返回 answer + summary_points + retrieved_posts
```

### 1. Query Analysis

AI 服务先分析用户问题，抽取几个关键信号：

- 问题类型
  - 趋势类
  - 筛选类
  - 探索类
- 地点
  - 上海 / 杭州 / 南京 等
- 主题词
  - 雨天 / 室内 / 拍照 / 周末 / 徒步 / 约会 等
- 时间倾向
  - 是否强调“最近”

### 2. Knowledge Retrieval

AI 服务不会直接查数据库，而是调用 Java 知识接口拉知识文档。

当前知识文档里包含：

- `postId`
- `title`
- `content`
- `location`
- `createTime`
- `likesCount`
- `commentCount`
- `topComments`

### 3. Candidate Ranking

当前不是向量召回，而是固定知识池上的轻量混合打分。

主要打分信号包括：

- 地点命中
- 主题命中
- 关键词重叠
- 热度信号
  - 点赞数
  - 评论数
- 时间新鲜度
- 趋势类问题的额外加权

### 4. Context Building

不会把全部帖子原文交给模型，而是只取 `top-k` 结果，组装成结构化证据。

证据中会保留：

- 帖子标题
- 地点
- 发布时间
- 点赞数
- 评论数
- 正文摘要
- 代表性评论
- 命中原因

这一步属于典型的 context engineering。

### 5. Answer Generation

模型只允许基于证据回答。

当前 prompt 约束的重点是：

- 不能脱离证据自由发挥
- 证据不足要明确说明
- 输出面向用户可读的总结
- 同时返回结构化 JSON，便于前端和调试使用

## 目前接口清单

### Java 后端

- `GET /agent/knowledge/posts`
  - 返回帖子知识池
  - 当前默认范围为 `24-60`

### AI 微服务

- `POST /api/content/generate`
  - 原有 AI 发帖文案生成接口
- `POST /api/agent/social-search`
  - 新增社交搜推 Agent 接口
- `GET /health`
  - 健康检查接口

## 当前 MVP 的边界

这版是“最小可用 Agent”，不是最终形态。

### 已经做到的

- 知识接口已经独立出来
- AI 服务已经具备 Query 分析、检索增强、证据构建、答案生成能力
- 保持了原文案生成接口兼容
- 已经可以在终端稳定测试

### 还没有做的

- 真正的全站搜索
- 向量数据库
- Hybrid Retrieval
- 长期记忆 / 用户记忆
- 主题卡片 / 周报 / 热点离线聚合
- 前端可视化 AI 搜推入口

### 当前已知限制

- 知识池现在是固定帖子范围，不是动态全站候选池
- 检索主要依赖规则与结构化信号，不是 embedding 检索
- 热度信号仍较轻，后续还可以进一步设计统一热度分

## 为什么这版仍然有价值

虽然还不是全站搜索系统，但它已经具备了 AI 原生系统的几个关键特征：

- 不是把所有文本直接丢给模型
- 有独立的知识环境层
- 有独立的检索层
- 有独立的上下文注入层
- 有可扩展的 Agent 编排骨架

这使它已经可以支撑如下项目表达：

- 面向业务需求设计 AI 原生系统架构
- 搭建 AI 与现有业务系统的交互环境
- 构建最小可用的 RAG / 检索增强链路
- 为后续记忆系统和向量库演进预留模块边界

## 本地测试方式

### 启动顺序

1. 启动 Redis
2. 启动 Java 后端
3. 启动 AI 服务

### 健康检查

可以先检查 AI 服务：

```powershell
Invoke-RestMethod http://127.0.0.1:8000/health
```

### 推荐测试方式

Windows PowerShell 对中文 JSON 的显示和落盘容易出现乱码，因此推荐使用项目根目录下的脚本：

- `test_social_agent.py`

运行方式：

```powershell
cd d:\VS\26project\SimpleNote_AI
python test_social_agent.py
```

自定义问题：

```powershell
python test_social_agent.py "雨天适合去哪些室内地方？"
```

脚本会：

- 请求 `POST /api/agent/social-search`
- 将完整结果写入 `agent-result.json`
- 在终端输出答案预览和召回帖子摘要

## 前端后续接入思路

前端后续最合理的第一版展示方式是：

- 展示 `answer`
- 展示 `summary_points`
- 展示 `retrieved_posts`

这样用户先看 AI 总结，如果想继续追溯来源，再点进具体帖子查看详情。

这比单纯只显示一段 AI 文本更可信，也更符合“AI 总结 + 证据来源”的产品形态。

## 面试时可以怎么讲

### 一句话版本

我把一个原本只支持 AI 文案生成的社区项目，扩成了一个具备最小知识环境、检索增强和证据约束回答能力的社交搜推 Agent。

### 稍展开版本

这个项目原本只有 AI 发帖文案生成功能。我在不破坏原能力的前提下，对 AI 微服务做了 feature 化重构，并在 Java 业务后端新增了知识环境层。当前用户提问后，AI 服务会先做 query analysis，再通过 Java 知识接口获取帖子知识文档，在固定语料池上做轻量混合检索，取 top-k 证据构建上下文，最后让模型输出基于站内内容的总结回答。这版虽然还没有接入向量数据库，但已经具备了后续演进到 RAG、记忆管理和工具编排的架构基础。

### 面试里要诚实说明的点

- 这是 MVP，不是完整的全站搜推系统
- 当前检索是规则混合检索，不是向量检索
- 当前知识池是固定帖子范围，不是全站动态候选池
- 但模块边界已经设计清楚，后续演进路径明确

## 后续演进路线

### 第一阶段

- 固定知识池
- 规则混合检索
- 证据包构建
- LLM 总结

### 第二阶段

- 从固定知识池升级成数据库动态候选池
- 引入更完整的地点 / 标签 / 主题抽取
- 提升热度和时间排序策略

### 第三阶段

- 给高价值帖子做 embedding
- 接入向量数据库
- 实现 hybrid retrieval

### 第四阶段

- 做主题记忆卡片
- 做热点日报 / 周报
- 做长期记忆与用户个性化上下文

## 当前最值得强调的工程价值

这次改造最重要的不是“功能多了一个接口”，而是项目的 AI 架构层级被拉开了：

- Java 后端：业务系统 + 知识环境
- AI 微服务：内容生成 + Agent 编排
- 检索、上下文、回答不再混成一团

这让项目从“调用大模型写文案”升级成了“具备最小 AI 系统架构雏形的内容社区平台”。




## 后续优化清单

### P0

- 给 Redis 主读接口补降级 facade
  - 目标：Redis 异常时能回退 MySQL，不把 `try Redis catch SQL` 散在业务层
- 给 Stream / DLQ 增加更明确的可观测性
  - 目标：记录 pending 数、DLQ 数、reclaim 次数、投影失败次数
- 给关键异步链补集成验证
  - 目标：覆盖“点赞成功 -> Stream -> MySQL 投影 -> dirty 补偿”主路径

### P1

- 优化 dirty 补偿策略
  - 目标：减少和主消费链的重复投影，后面可考虑版本 / 水位机制
- 抽统一 read model facade
  - 例如 `PostInteractionReadService`、`FollowReadService`
- 整理帖子流 / 帖子详情读模型边界
  - 明确哪些字段是 MySQL 主体，哪些字段是 Redis 实时覆盖

### P2

- 评论点赞接入同一套 Redis + Stream + 补偿链
- 评估关注数是否需要反规范化到用户表
- 如果后续压力上来，再考虑帖子流是否演进成“Redis 主索引 + MySQL 补详情”
