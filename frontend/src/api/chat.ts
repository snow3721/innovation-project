import request from './request'

export interface ConversationVO {
  conversationId: number
  otherUserId: number
  otherUserName: string
  unreadCount: number
  lastMessage: string | null
  lastMessageTime: string | null
  online: boolean
}

export interface ChatMessageVO {
  id: string
  conversationId: number
  senderId: number
  content: string
  sendTime: string
}

export function getConversations() {
  return request.get('/chat/conversations')
}

export function getChatMessages(conversationId: number, page = 1, size = 20) {
  return request.get(`/chat/conversations/${conversationId}/messages`, { params: { page, size } })
}

export function sendChatMessage(data: { receiverId: number; content: string }) {
  return request.post('/chat/messages', data)
}

export function markConversationRead(conversationId: number) {
  return request.put(`/chat/conversations/${conversationId}/read`)
}

export function deleteConversation(conversationId: number) {
  return request.delete(`/chat/conversations/${conversationId}`)
}
