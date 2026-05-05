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

export function teacherAudit(data: { projectId: number; result: string; opinion?: string }) {
  return request.post('/projects/teacher-audit', data)
}

export function collegeAudit(data: { projectId: number; result: string; opinion?: string }) {
  return request.post('/projects/college-audit', data)
}

export function schoolAudit(data: { projectId: number; result: string; opinion?: string }) {
  return request.post('/projects/school-audit', data)
}

export function submitMidCheck(projectId: number) {
  return request.post(`/projects/${projectId}/mid-check`)
}

export function auditMidCheck(midId: number, data: { result: string; opinion?: string }) {
  return request.post(`/projects/mid-check/${midId}/audit`, data)
}

export function getMidChecks(params: any) {
  return request.get('/projects/mid-checks', { params })
}

export function submitConclude(projectId: number) {
  return request.post(`/projects/${projectId}/conclude`)
}

export function auditConclude(concludeId: number, data: { result: string; opinion?: string }) {
  return request.post(`/projects/conclude/${concludeId}/audit`, data)
}

export function getConcludes(params: any) {
  return request.get('/projects/concludes', { params })
}
