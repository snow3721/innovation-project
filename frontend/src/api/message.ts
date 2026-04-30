import request from './request'

export function getMessages(params: { page: number; size: number; type?: string; isRead?: number }) {
  return request.get('/messages', { params })
}

export function getUnreadCount() {
  return request.get('/messages/unread-count')
}

export function markRead(id: number) {
  return request.put(`/messages/${id}/read`)
}

export function markAllRead() {
  return request.put('/messages/read-all')
}

export function deleteMessage(id: number) {
  return request.delete(`/messages/${id}`)
}

export function sendMessage(data: { receiverId: number; title: string; content: string; type?: string; relationId?: number }) {
  return request.post('/messages/send', data)
}
