import request from './request'

export function getColleges() {
  return request.get('/colleges')
}

export function createCollege(data: any) {
  return request.post('/colleges', data)
}

export function updateCollege(id: number, data: any) {
  return request.put(`/colleges/${id}`, data)
}

export function deleteCollege(id: number) {
  return request.delete(`/colleges/${id}`)
}
