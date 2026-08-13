## 1. 后端基础设施

- [x] 1.1 添加 `spring-boot-starter-websocket` 依赖到 pom.xml
- [x] 1.2 创建 WebSocket 配置类 `WebSocketConfig`（STOMP 端点 `/ws/chat`，SockJS 降级）
- [x] 1.3 创建 WebSocket 认证拦截器 `WebSocketAuthInterceptor`（校验 JWT token）
- [x] 1.4 创建在线状态管理器 `OnlineStatusManager`（ConcurrentHashMap 维护 userId -> sessionId 集合）
- [x] 1.5 创建在线状态事件监听器 `WebSocketEventListener`（处理 SessionConnectEvent / SessionDisconnectEvent）

## 2. 后端数据层

- [x] 2.1 创建 MySQL 实体 `Conversation`（会话表：conversation_id, 创建时间）和 `ConversationParticipant`（参与者表：conversation_id, user_id, 未读数, 删除标记）
- [x] 2.2 编写 schema.sql 新增 DDL（`conversation`、`conversation_participant` 表）
- [x] 2.3 创建 MongoDB 文档类 `ChatMessage`（conversationId, senderId, content, sendTime）
- [x] 2.4 创建 MyBatis Mapper `ConversationMapper` 和 `ConversationParticipantMapper`（含 XML）
- [x] 2.5 创建 MongoDB Repository `ChatMessageRepository`

## 3. 后端业务层

- [x] 3.1 创建 DTO 类（`ChatSendDTO`、`ConversationVO`、`ChatMessageVO`）
- [x] 3.2 创建 `ChatService` 接口和 `ChatServiceImpl` 实现（会话管理、消息发送/查询）
- [x] 3.3 创建 `ChatController`（REST API：会话列表、消息历史、发送消息、删除会话、标记已读）
- [x] 3.4 创建 WebSocket 消息处理器（STOMP `@MessageMapping` 接收消息，`SimpMessagingTemplate` 推送）
- [x] 3.5 编写单元测试 `ChatServiceTest`（TDD：先写失败测试，再写实现）

## 4. 前端基础设施

- [x] 4.1 安装前端依赖 `@stomp/stompjs` 和 `sockjs-client`
- [x] 4.2 创建 API 模块 `api/chat.ts`（会话列表、消息历史、发送消息、标记已读 REST 接口）
- [x] 4.3 创建 Pinia store `stores/chat.ts`（会话列表、未读总数、当前会话状态）
- [x] 4.4 创建 composable `composables/useChatSocket.ts`（STOMP 连接管理、消息订阅、自动重连）

## 5. 前端页面

- [x] 5.1 创建 `views/chat/ChatLayout.vue`（左侧会话列表 + 右侧聊天窗口的 split 布局）
- [x] 5.2 创建 `views/chat/components/ChatList.vue`（会话列表组件：头像、名称、最后消息摘要、未读徽标）
- [x] 5.3 创建 `views/chat/components/ChatWindow.vue`（聊天窗口：消息气泡、输入框、发送按钮）
- [x] 5.4 创建 `views/chat/components/UserSelector.vue`（用户选择器：搜索用户并发起新会话）

## 6. 前端路由与集成

- [x] 6.1 在 `router/index.ts` 新增路由 `/chat` 和 `/chat/:conversationId`，指向 `ChatLayout.vue`
- [x] 6.2 在 `AdminLayout.vue` 侧边栏新增"站内信"菜单项（ChatDotIcon），显示总未读计数徽标
- [x] 6.3 编写前端测试 `ChatSocket.test.ts`（Vitest：WebSocket 连接、消息收发、store 状态更新）

## 7. 集成验证

- [ ] 7.1 启动后端，验证 WebSocket 端点和 REST API 可用
- [ ] 7.2 启动前端，验证双用户实时聊天流程
- [ ] 7.3 前后端联调：验证离线消息、未读计数、在线状态
