import request from './request'

export function getAchievements(params: any) {
  return request.get('/achievements', { params })
}

export function getAchievement(id: number) {
  return request.get(`/achievements/${id}`)
}

export function createAchievement(data: any) {
  return request.post('/achievements', data)
}

export function updateAchievement(id: number, data: any) {
  return request.put(`/achievements/${id}`, data)
}

export function deleteAchievement(id: number) {
  return request.delete(`/achievements/${id}`)
}
