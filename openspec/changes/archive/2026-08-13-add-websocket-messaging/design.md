## Context

Spring Boot 2.7.18 (Java 11)，MySQL(结构化数据) + MongoDB(大文本/动态字段)，JWT 认证，Vue 3 + TypeScript + Element Plus 前端。现有 `message` 模块为单向系统通知，与本次需求无关。参见 proposal.md。

## Goals / Non-Goals

**Goals:**
- 用户间一对一实时私信，WebSocket 推送
- 独立前端入口和页面，不影响现有消息中心
- 消息持久化，支持离线消息和历史加载

**Non-Goals:**
- 群聊、文件传输、消息已读回执
- 替换现有系统通知模块

## Decisions

### 1. WebSocket 方案：Spring WebSocket (STOMP over SockJS)

**选择**: `spring-boot-starter-websocket` + STOMP 子协议 + SockJS 降级

**理由**: Spring Boot 2.7 原生支持，无需额外依赖；STOMP 提供发布/订阅模型，消息路由清晰；SockJS 自动处理浏览器兼容性和代理穿透。

**替代方案**: 原生 WebSocket（`javax.websocket`）——消息路由需手动管理，代码量大；Netty-SocketIO —— 额外依赖，与 Spring 生态集成复杂度高。

### 2. 数据存储：MySQL 存会话元数据，MongoDB 存聊天消息

**选择**: 会话和参与者信息存 MySQL（`conversation` 表），聊天消息存 MongoDB（`chat_message` 集合）

**理由**: 符合项目 AGENTS.md 的分层策略——结构化核心数据入 MySQL，大文本/动态字段/高写入频率数据入 MongoDB。聊天消息持续增长、写入频繁，MongoDB 的文档模型和水平扩展更适合。

**替代方案**: 全部入 MySQL——消息表会快速增长，影响查询性能；全部入 MongoDB——会话这类关系型查询（JOIN 用户表）在 MongoDB 中不自然。

### 3. WebSocket 认证：JWT 通过 STOMP CONNECT 头传递

**选择**: 前端在 STOMP CONNECT 帧的 `Authorization` 头中携带 JWT token，后端通过 `ChannelInterceptor` 校验

**理由**: 复用现有 JWT 体系，无需额外认证机制。Spring WebSocket 的 `ChannelInterceptorAdapter` 在 CONNECT 阶段可拦截校验。

### 4. 在线状态：服务端内存 Map + WebSocket 连接/断开事件

**选择**: 后端维护 `ConcurrentHashMap<Integer, Set<String>>`（userId -> sessionId 集合），通过 `SessionConnectEvent`/`SessionDisconnectEvent` 维护

**理由**: 简单可靠，无需引入 Redis。项目规模（高校内部系统）不需要分布式在线状态。

### 5. 前端 WebSocket 管理：Composable + Pinia Store

**选择**: `@stomp/stompjs` + `sockjs-client`，封装为 Vue composable `useChatSocket`，状态由 `stores/chat.ts` 管理

**理由**: `@stomp/stompjs` 是 STOMP 客户端的事实标准，TypeScript 支持好；composable 模式符合 Vue 3 组合式 API 风格；Pinia 集中管理会话列表和未读计数。

## Risks / Trade-offs

- [R1] WebSocket 连接中断后消息可能丢失 → 发送消息采用"先存后推"策略：消息先持久化到 MongoDB，再通过 WebSocket 推送；接收方上线后从 MongoDB 拉取历史消息
- [R2] 服务重启导致在线状态丢失 → 可接受，重启后所有用户重新连接即可恢复在线状态
- [R3] 单服务器内存存储在线状态，多实例部署时不共享 → 当前部署为单实例（docker-compose），如有扩展需求后续可迁移到 Redis

## Migration Plan

1. 后端新增依赖和配置，创建新表/集合，部署新代码
2. 前端新增路由和页面，部署新代码
3. 无需数据迁移——现有 `message` 表不受影响
4. 回滚：回退代码版本，删除新增的 MySQL 表和 MongoDB 集合即可
