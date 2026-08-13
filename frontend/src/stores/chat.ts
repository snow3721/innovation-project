import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { ConversationVO } from '@/api/chat'

export const useChatStore = defineStore('chat', () => {
  const conversations = ref<ConversationVO[]>([])
  const currentConversationId = ref<number | null>(null)
  const currentMessages = ref<any[]>([])
  const totalUnread = computed(() =>
    conversations.value.reduce((sum, c) => sum + (c.unreadCount || 0), 0)
  )

  function setConversations(list: ConversationVO[]) {
    conversations.value = list
  }

  function upsertConversation(conversation: ConversationVO) {
    const index = conversations.value.findIndex(c => c.conversationId === conversation.conversationId)
    if (index >= 0) {
      conversations.value[index] = conversation
    } else {
      conversations.value.unshift(conversation)
    }
  }

  function setCurrentConversation(id: number | null) {
    currentConversationId.value = id
  }

  function setCurrentMessages(messages: any[]) {
    currentMessages.value = messages
  }

  function appendMessage(message: any) {
    currentMessages.value.push(message)
  }

  function clearUnread(conversationId: number) {
    const c = conversations.value.find(item => item.conversationId === conversationId)
    if (c) {
      c.unreadCount = 0
    }
  }

  function reset() {
    conversations.value = []
    currentConversationId.value = null
    currentMessages.value = []
  }

  return {
    conversations,
    currentConversationId,
    currentMessages,
    totalUnread,
    setConversations,
    upsertConversation,
    setCurrentConversation,
    setCurrentMessages,
    appendMessage,
    clearUnread,
    reset
  }
})
