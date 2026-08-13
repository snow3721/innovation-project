<template>
  <div class="chat-list">
    <el-empty v-if="conversations.length === 0" description="暂无会话" :image-size="60" />
    <div
      v-for="c in conversations"
      :key="c.conversationId"
      class="conversation-item"
      :class="{ active: c.conversationId === currentId }"
      @click="$emit('select', c)"
    >
      <div class="avatar-wrap">
        <el-avatar :size="40">{{ c.otherUserName?.charAt(0) }}</el-avatar>
        <span v-if="c.online" class="online-dot" />
      </div>
      <div class="conv-body">
        <div class="conv-top">
          <span class="conv-name">{{ c.otherUserName }}</span>
          <span class="conv-time">{{ c.lastMessageTime || '' }}</span>
        </div>
        <div class="conv-bottom">
          <span class="conv-preview">{{ c.lastMessage || '暂无消息' }}</span>
          <el-badge v-if="c.unreadCount > 0" :value="c.unreadCount" :max="99" />
        </div>
      </div>
      <el-button
        class="delete-btn"
        :icon="Delete"
        link
        size="small"
        @click.stop="$emit('delete', c)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { Delete } from '@element-plus/icons-vue'
import type { ConversationVO } from '@/api/chat'

defineProps<{
  conversations: ConversationVO[]
  currentId: number | null
}>()

defineEmits<{
  select: [conversation: ConversationVO]
  delete: [conversation: ConversationVO]
}>()
</script>

<style lang="scss" scoped>
.chat-list {
  flex: 1;
  overflow-y: auto;
  padding: 6px;
}

.conversation-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 8px;
  border-radius: 6px;
  cursor: pointer;
  position: relative;

  &:hover {
    background: var(--el-fill-color-light);
  }

  &.active {
    background: var(--el-color-primary-light-9);
  }

  .avatar-wrap {
    position: relative;
    flex-shrink: 0;

    .online-dot {
      position: absolute;
      right: 0;
      bottom: 0;
      width: 10px;
      height: 10px;
      background: #67c23a;
      border: 2px solid #fff;
      border-radius: 50%;
    }
  }

  .conv-body {
    flex: 1;
    min-width: 0;
  }

  .conv-top {
    display: flex;
    justify-content: space-between;
    gap: 6px;
    margin-bottom: 4px;

    .conv-name {
      font-size: 14px;
      font-weight: 600;
      color: var(--text-primary);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .conv-time {
      font-size: 11px;
      color: var(--el-text-color-secondary);
      white-space: nowrap;
    }
  }

  .conv-bottom {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 6px;

    .conv-preview {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      flex: 1;
    }
  }

  .delete-btn {
    opacity: 0;
    transition: opacity 0.2s;
  }

  &:hover .delete-btn {
    opacity: 1;
  }
}
</style>