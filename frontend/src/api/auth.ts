import request from './request'

export function login(data: { username: string; password: string }) {
  return request.post('/auth/login', data)
}

export function register(data: { username: string; password: string; realName: string; role: string; phone?: string; email?: string; collegeId?: number; major?: string }) {
  return request.post('/auth/register', data)
}

export function logout() {
  return request.post('/auth/logout')
}
