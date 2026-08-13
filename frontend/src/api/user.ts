import request from './request'

export function getUsers(params: any) {
  return request.get('/users', { params })
}

export function getUser(id: number) {
  return request.get(`/users/${id}`)
}

export function createUser(data: any) {
  return request.post('/users', data)
}

export function updateUser(id: number, data: any) {
  return request.put(`/users/${id}`, data)
}

export function deleteUser(id: number) {
  return request.delete(`/users/${id}`)
}

export function getCurrentUser() {
  return request.get('/users/me')
}

export function getTeachers(params?: { collegeId?: number; realName?: string }) {
  return request.get('/users/teachers', { params })
}

export function searchUsers(keyword?: string) {
  return request.get('/users/search', { params: { keyword } })
}

export function changePassword(data: { oldPassword: string; newPassword: string }) {
  return request.put('/users/change-password', data)
}
