import request from './request'

export function getMilestones(params: any) {
  return request.get('/milestones', { params })
}

export function createMilestone(data: any) {
  return request.post('/milestones', data)
}

export function updateMilestone(id: number, data: any) {
  return request.put(`/milestones/${id}`, data)
}

export function deleteMilestone(id: number) {
  return request.delete(`/milestones/${id}`)
}
