import request from './request'

export function getExperts(params: any) {
  return request.get('/experts', { params })
}

export function createExpert(data: any) {
  return request.post('/experts', data)
}

export function updateExpert(id: number, data: any) {
  return request.put(`/experts/${id}`, data)
}

export function deleteExpert(id: number) {
  return request.delete(`/experts/${id}`)
}
