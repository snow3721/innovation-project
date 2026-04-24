import request from './request'

export function getProjects(params: any) {
  return request.get('/projects', { params })
}

export function getProject(id: number) {
  return request.get(`/projects/${id}`)
}

export function createProject(data: any) {
  return request.post('/projects', data)
}

export function updateProject(id: number, data: any) {
  return request.put(`/projects/${id}`, data)
}

export function deleteProject(id: number) {
  return request.delete(`/projects/${id}`)
}

export function submitProject(id: number) {
  return request.post(`/projects/${id}/submit`)
}

export function teacherAudit(data: { projectId: number; result: string }) {
  return request.post('/projects/teacher-audit', data)
}

export function collegeAudit(data: { projectId: number; result: string }) {
  return request.post('/projects/college-audit', data)
}

export function schoolAudit(data: { projectId: number; result: string }) {
  return request.post('/projects/school-audit', data)
}
