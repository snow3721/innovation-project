<template>
  <div class="chat-layout">
    <div class="chat-sidebar">
      <div class="chat-sidebar-header">
        <span class="chat-title">站内信</span>
        <el-button :icon="Plus" circle size="small" type="primary" @click="showSelector = true" />
      </div>
      <ChatList
        :conversations="chatStore.conversations"
        :current-id="chatStore.currentConversationId"
        @select="handleSelect"
        @delete="handleDelete"
      />
    </div>

    <div class="chat-main">
      <ChatWindow
        :conversation="currentConversation"
        :pending-receiver="pendingReceiver"
        :messages="chatStore.currentMessages"
        :connected="connected"
        @send="handleSend"
      />
    </div>

    <el-dialog v-model="showSelector" title="发起新会话" width="420px">
      <UserSelector @select="handleStartChat" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import ChatList from './components/ChatList.vue'
import ChatWindow from './components/ChatWindow.vue'
import UserSelector from './components/UserSelector.vue'
import { useChatStore } from '@/stores/chat'
import { useChatSocket } from '@/composables/useChatSocket'
import { getConversations, getChatMessages, sendChatMessage, markConversationRead, deleteConversation } from '@/api/chat'

const chatStore = useChatStore()
const socket = useChatSocket()
const connected = computed(() => socket.connected.value)
const showSelector = ref(false)
const pendingReceiver = ref<any>(null)

const currentConversation = computed(() =>
  chatStore.conversations.find(c => c.conversationId === chatStore.currentConversationId) || null
)

async function loadConversations() {
  try {
    const res: any = await getConversations()
    chatStore.setConversations(res.data || [])
  } catch {}
}

async function handleSelect(conversation: any) {
  chatStore.setCurrentConversation(conversation.conversationId)
  pendingReceiver.value = null
  try {
    const res: any = await getChatMessages(conversation.conversationId)
    chatStore.setCurrentMessages(res.data || [])
    if (conversation.unreadCount > 0) {
      await markConversationRead(conversation.conversationId)
      chatStore.clearUnread(conversation.conversationId)
    }
  } catch {}
}

async function handleStartChat(user: any) {
  const existing = chatStore.conversations.find(c => c.otherUserId === user.userId)
  if (existing) {
    await handleSelect(existing)
  } else {
    chatStore.setCurrentConversation(null)
    chatStore.setCurrentMessages([])
    pendingReceiver.value = user
  }
  showSelector.value = false
}

async function handleSend(content: string) {
  const receiverId = currentConversation.value?.otherUserId || pendingReceiver.value?.userId
  if (!receiverId) return

  const sent = socket.sendMessage(receiverId, content)
  if (!sent) {
    try {
      await sendChatMessage({ receiverId, content })
    } catch {}
  }

  if (pendingReceiver.value) {
    await loadConversations()
    const conv = chatStore.conversations.find(c => c.otherUserId === receiverId)
    if (conv) {
      chatStore.setCurrentConversation(conv.conversationId)
      pendingReceiver.value = null
      try {
        const res: any = await getChatMessages(conv.conversationId)
        chatStore.setCurrentMessages(res.data || [])
      } catch {}
    }
  }
}

async function handleDelete(conversation: any) {
  await ElMessageBox.confirm('删除会话不会影响对方的会话，确定删除？', '提示', { type: 'warning' })
  try {
    await deleteConversation(conversation.conversationId)
    if (chatStore.currentConversationId === conversation.conversationId) {
      chatStore.setCurrentConversation(null)
      chatStore.setCurrentMessages([])
    }
    await loadConversations()
    ElMessage.success('会话已删除')
  } catch {}
}

onMounted(() => {
  loadConversations()
})
</script>

<style lang="scss" scoped>
.chat-layout {
  display: flex;
  height: calc(100vh - 100px);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.chat-sidebar {
  width: 280px;
  border-right: 1px solid var(--el-border-color-light);
  display: flex;
  flex-direction: column;
}

.chat-sidebar-header {
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 14px;
  border-bottom: 1px solid var(--el-border-color-light);

  .chat-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
  }
}

.chat-main {
  flex: 1;
  display: flex;
  min-width: 0;
}
</style>