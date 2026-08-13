<template>
  <div class="chat-window">
    <div class="chat-window-header">
      <div v-if="conversation || pendingReceiver" class="peer-info">
        <el-avatar :size="32">{{ peerName?.charAt(0) }}</el-avatar>
        <div>
          <div class="peer-name">{{ peerName }}</div>
          <div class="peer-status" :class="{ online: conversation?.online }">
            {{ conversation?.online ? '在线' : '离线' }}
          </div>
        </div>
      </div>
      <div v-else class="empty-header">选择一个会话开始聊天</div>
    </div>

    <div ref="messageBox" class="message-box">
      <el-empty v-if="!conversation && !pendingReceiver" description="选择左侧会话开始聊天" />
      <el-empty v-else-if="messages.length === 0" description="发送第一条消息吧" />
      <div
        v-for="m in messages"
        :key="m.id"
        class="message-row"
        :class="{ mine: m.senderId === userId }"
      >
        <div class="bubble">{{ m.content }}</div>
        <div class="message-time">{{ m.sendTime }}</div>
      </div>
    </div>

    <div class="chat-input">
      <el-input
        v-model="draft"
        type="textarea"
        :rows="2"
        resize="none"
        placeholder="输入消息..."
        @keydown.enter.exact.prevent="submit"
      />
      <div class="input-actions">
        <span v-if="!connected" class="offline-tip">连接已断开，重连中...</span>
        <el-button type="primary" :disabled="!canSend" @click="submit">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import type { ConversationVO } from '@/api/chat'

const props = defineProps<{
  conversation: ConversationVO | null
  pendingReceiver: any
  messages: any[]
  connected: boolean
}>()

const emit = defineEmits<{
  send: [content: string]
}>()

const userStore = useUserStore()
const draft = ref('')
const messageBox = ref<HTMLElement>()

const userId = computed(() => userStore.userId)
const peerName = computed(() => props.conversation?.otherUserName || props.pendingReceiver?.realName || '')
const canSend = computed(() => draft.value.trim().length > 0)

function submit() {
  const content = draft.value.trim()
  if (!content) return
  emit('send', content)
  draft.value = ''
}

watch(() => props.messages.length, async () => {
  await nextTick()
  if (messageBox.value) {
    messageBox.value.scrollTop = messageBox.value.scrollHeight
  }
})
</script>

<style lang="scss" scoped>
.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.chat-window-header {
  height: 52px;
  border-bottom: 1px solid var(--el-border-color-light);
  padding: 0 16px;
  display: flex;
  align-items: center;

  .peer-info {
    display: flex;
    align-items: center;
    gap: 10px;

    .peer-name {
      font-size: 14px;
      font-weight: 600;
      color: var(--text-primary);
    }

    .peer-status {
      font-size: 12px;
      color: var(--el-text-color-secondary);

      &.online {
        color: #67c23a;
      }
    }
  }

  .empty-header {
    font-size: 14px;
    color: var(--el-text-color-secondary);
  }
}

.message-box {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: var(--el-fill-color-lighter);

  .message-row {
    display: flex;
    flex-direction: column;
    margin-bottom: 12px;

    &.mine {
      align-items: flex-end;

      .bubble {
        background: var(--el-color-primary);
        color: #fff;
      }
    }

    .bubble {
      max-width: 65%;
      padding: 8px 12px;
      border-radius: 8px;
      background: #fff;
      color: var(--text-primary);
      font-size: 14px;
      word-break: break-word;
      box-shadow: var(--el-box-shadow-lighter);
    }

    .message-time {
      font-size: 11px;
      color: var(--el-text-color-secondary);
      margin-top: 3px;
    }
  }
}

.chat-input {
  border-top: 1px solid var(--el-border-color-light);
  padding: 12px 16px;

  .input-actions {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 8px;

    .offline-tip {
      font-size: 12px;
      color: var(--el-color-warning);
    }
  }
}
</style>