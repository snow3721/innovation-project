import { describe, it, expect, beforeEach } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

describe('ChatStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('should start with empty conversations and zero unread', async () => {
    const { useChatStore } = await import('@/stores/chat')
    const store = useChatStore()
    expect(store.conversations).toEqual([])
    expect(store.totalUnread).toBe(0)
  })

  it('should upsert conversations and track unread total', async () => {
    const { useChatStore } = await import('@/stores/chat')
    const store = useChatStore()

    store.upsertConversation({
      conversationId: 1,
      otherUserId: 2,
      otherUserName: 'user-b',
      unreadCount: 3,
      lastMessage: 'hi',
      lastMessageTime: '2026-08-13T10:00:00',
      online: true
    } as any)

    store.upsertConversation({
      conversationId: 2,
      otherUserId: 3,
      otherUserName: 'user-c',
      unreadCount: 2,
      lastMessage: 'hello',
      lastMessageTime: '2026-08-13T10:05:00',
      online: false
    } as any)

    expect(store.conversations.length).toBe(2)
    expect(store.totalUnread).toBe(5)
  })

  it('should clear unread for a conversation', async () => {
    const { useChatStore } = await import('@/stores/chat')
    const store = useChatStore()

    store.setConversations([
      {
        conversationId: 1,
        otherUserId: 2,
        otherUserName: 'user-b',
        unreadCount: 5,
        lastMessage: 'hi',
        lastMessageTime: null,
        online: true
      } as any
    ])

    store.clearUnread(1)
    expect(store.totalUnread).toBe(0)
  })

  it('should append a message to current messages', async () => {
    const { useChatStore } = await import('@/stores/chat')
    const store = useChatStore()

    store.setCurrentMessages([])
    store.appendMessage({ id: 'm1', conversationId: 1, senderId: 2, content: 'hello', sendTime: '2026-08-13T10:00:00' })
    expect(store.currentMessages.length).toBe(1)
    expect(store.currentMessages[0].content).toBe('hello')
  })
})
