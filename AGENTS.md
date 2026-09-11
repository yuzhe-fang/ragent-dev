# AGENTS.md — ragent-dev

Ragent AI：企业级 RAG 问答系统（Agent + 知识库 + 多路检索）。
本仓库 fork 自 [nageoffer/ragent](https://github.com/nageoffer/ragent)（Apache-2.0），是我二次开发的学习项目。
官方本地搭建文档：https://nageoffer.com/ragent/local-dev/

## 我在这个仓库的模式：学习与分析（读优先）

- 我主要**读懂它**并为二次开发做准备，读代码优先于改代码。
- 我提问时**先答"为什么"，再答"怎么做"**；不要直接甩改写后的整段代码。
- 真要改代码时，先给**改动方案 + 影响范围 + 风险评估**，等我确认再动手。
- 讲解请顺调用链走：**请求入口 → 关键类 → 数据流**，不要按目录顺序念文件。
- 源码即文档，优先让我自己读代码；你负责指路和解释，不要替我总结全量实现。

## 技术栈（以 `pom.xml` / `frontend/package.json` 为准）

| 层 | 技术 | 版本 |
|---|---|---|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 4.1.0 |
| ORM | MyBatis-Plus | 3.5.17 |
| 向量库 | Milvus（Java SDK） | 2.6.6 |
| 消息 | RocketMQ | 5.2.0 |
| 关系库 | PostgreSQL | 见 `resources/database/` |
| 前端 | React + Vite + Tailwind + shadcn/ui | React 18 |

## 命令

```bash
# 编译（跳过测试）
./mvnw -q -DskipTests clean install

# 只测某个模块
./mvnw -pl rag test

# 启动整个服务（bootstrap 是启动装配层）
./mvnw -pl bootstrap -am spring-boot:run

# 前端
cd frontend && npm run dev
cd frontend && npm run build
cd frontend && npm run lint
```

```bash
# 本地依赖服务（Intel Mac 用 -amd 版 RocketMQ）
docker compose -f resources/docker/milvus-stack-2.6.6.compose.yaml up -d
docker compose -f resources/docker/rocketmq-stack-amd-5.2.0.compose.yaml up -d
```

> ⚠️ 我的机器是 **Intel Mac（x86_64）**，RocketMQ 必须用带 `-amd` 的那个 compose 文件。
> ⚠️ 我本机**尚未安装 Docker**，涉及容器相关操作先提醒我，不要假定能跑。

## 模块地图（README「核心设计」原文）

| 模块 | 职责 |
|---|---|
| `framework` | 统一响应与异常、认证上下文、幂等、分布式 ID、MQ 适配、Trace、SSE 与跨节点流式取消等通用基础能力（**动它会波及全局，改动需先评估**） |
| `infra-ai` | Chat / Embedding / Rerank / VLM 模型客户端、模型档位、路由、首包探测、健康状态与降级 |
| `system` | 用户认证与审计日志，位于各业务引擎之下的系统支撑域 |
| `rag` | RAG 问答、知识库、入库 Pipeline、意图树、检索、会话及管理端 API（**主战场**） |
| `agent` | Agent 执行架构（v2 ReAct）骨架，RAG 管线将以工具形式接入 |
| `bootstrap` | 启动装配层，仅含启动类与主配置（启动类 `com.nageoffer.ai.ragent.RagentApplication`） |
| `mcp-server` | 基于 MCP Java SDK 的独立工具服务，内置天气、票务、销售与联网搜索示例 |

其他目录：

| 目录 | 用途 |
|---|---|
| `frontend/` | React 前端（Vite + Tailwind + shadcn/ui），有 `TESTING.md` 说明测试方式 |
| `resources/database/` | PostgreSQL 初始化脚本 `init_data_pg.sql`、`schema_pg.sql` 与 `upgrades/` 迁移 |
| `resources/docker/` | Milvus / RocketMQ / Langfuse 等本地依赖编排 |
| `docs/` | 项目文档（含 `examples/`、`releases/`） |
| `scripts/` | 辅助脚本，如 `sse_queue_test.sh` |

设计意图：把**业务编排、AI 供应商差异、通用基础设施**三层隔离开——换模型 / 换向量库 / 换对象存储时，核心问答流程不需要重写。理解每一层的边界是这个项目的学习重点。

## 关键约定

- 想搞懂一条链路，从 `bootstrap` 启动类出发，再进 `rag` 找对应的 Controller。
- 模型调用统一走 `infra-ai` 的客户端封装，不要在业务代码里直接 new SDK 客户端。
- 跨模块公共能力看 `framework`，不要在业务模块里复制一份。
- 敏感配置（API Key、库密码）走配置文件 / 环境变量，不硬编码。

## 绝对不做

- 不改 `resources/database/schema_pg.sql`（演进走 `upgrades/` 下的迁移脚本）
- 不改 `assets/` 里的图片、不改 `LICENSE`
- 不擅自升级依赖版本 / 不换框架；需要时先说明理由
- 不为"跑通"而注释掉核心逻辑、加空 catch、或把 Milvus/RocketMQ 换成假实现
- 没实际读代码前，不假设某个模块干什么（尤其是 `rag` 和 `infra-ai`）

## 合规提醒（Apache-2.0 义务）

- `LICENSE` 必须保留、不得删除
- 源码中的 copyright 声明不得抹除
- 二次开发需在 README 中声明"基于 nageoffer/ragent 二次开发"

## 我的背景

Java 后端背景，Spring 生态熟悉；**RAG / 向量检索 / Agent 架构和前端是我要补的部分**。
讲到这些请多给背景和类比，按「这段在做什么 → 为什么这么写 → 换别的写法会怎样」三层展开。
