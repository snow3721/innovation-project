import { ref, onMounted, onUnmounted } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useUserStore } from '@/stores/user'
import { useChatStore } from '@/stores/chat'
import type { ConversationVO } from '@/api/chat'

export function useChatSocket() {
  const userStore = useUserStore()
  const chatStore = useChatStore()
  const connected = ref(false)
  let client: Client | null = null

  function connect() {
    const token = userStore.token
    if (!token) return

    client = new Client({
      webSocketFactory: () => new SockJS('/ws/chat'),
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        connected.value = true
        client?.subscribe('/user/queue/messages', (message) => {
          const payload = JSON.parse(message.body)
          handleIncoming(payload)
        })
      },
      onDisconnect: () => {
        connected.value = false
      },
      onStompError: () => {
        connected.value = false
      }
    })

    client.activate()
  }

  function handleIncoming(message: any) {
    const currentId = chatStore.currentConversationId
    if (currentId && message.conversationId === currentId) {
      chatStore.appendMessage(message)
    } else {
      const c = chatStore.conversations.find(item => item.conversationId === message.conversationId)
      if (c) {
        c.unreadCount = (c.unreadCount || 0) + 1
        c.lastMessage = message.content
        c.lastMessageTime = message.sendTime
      }
    }
  }

  function sendMessage(receiverId: number, content: string): boolean {
    if (!client || !client.connected || !content.trim()) {
      return false
    }
    client.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({ receiverId, content })
    })
    return true
  }

  function disconnect() {
    client?.deactivate()
    client = null
    connected.value = false
  }

  onMounted(() => {
    connect()
  })

  onUnmounted(() => {
    disconnect()
  })

  return { connected, connect, disconnect, sendMessage }
}
