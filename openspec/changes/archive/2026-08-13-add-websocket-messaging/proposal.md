## Why

系统当前仅有单向的系统通知消息（`message` 表/模块），缺少用户之间实时沟通的能力。在项目协作过程中，学生、导师、管理员之间需要快速交流（如讨论项目细节、协调评审时间），而系统通知无法满足这种双向即时对话的需求。

## What Changes

- 新增**站内聊天模块**（与现有"消息中心"系统通知完全独立），提供用户间实时私信功能
- 新增 WebSocket 基础设施，支持实时消息推送和在线状态感知
- 新增聊天会话（Conversation）和聊天消息（ChatMessage）数据模型，聊天消息存储于 MongoDB（大文本、动态字段、高写入频率）
- 新增独立的前端聊天入口：侧边栏"站内信"菜单项，独立聊天页面（会话列表 + 聊天窗口布局）
- 新增后端 REST API（`/api/v1/chat/*`）和 WebSocket 端点（`/ws/chat`），通过 JWT 认证
- 新增前端聊天状态管理（Pinia store）和 WebSocket 连接管理

## Capabilities

### New Capabilities
- `chat`: 用户间实时私信聊天，包含会话管理、消息收发、在线状态、未读计数

### Modified Capabilities
<!-- 现有 capability 的需求无变化 -->

## Impact

- **后端新增**：`ChatController`、`ChatService`/`ChatServiceImpl`、MongoDB 文档实体（`ChatMessage`）、MySQL 实体/表（`conversation`、`conversation_participant`）、WebSocket 配置（`WebSocketConfig`）、WebSocket 拦截器（`ChatWebSocketHandler`）、DTO 类
- **前端新增**：`views/chat/`（`ChatLayout.vue`、`ChatList.vue`）、`api/chat.ts`、`stores/chat.ts`、WebSocket 连接管理 composable
- **前端修改**：`router/index.ts` 新增路由、`AdminLayout.vue` 新增侧边栏菜单项
- **依赖新增**：`spring-boot-starter-websocket`（Spring Boot 2.7 内置）
- **基础设施**：MongoDB（已有，聊天消息存储）、MySQL（新增 conversation 表）
- **无** BREAKING 变更，现有系统消息模块不受影响
