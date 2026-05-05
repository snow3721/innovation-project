import request from './request'

export function getReviewScores(params: any) {
  return request.get('/reviews/scores', { params })
}

export function submitScore(data: any) {
  return request.post('/reviews/scores', data)
}

export function getAssignments(params: any) {
  return request.get('/reviews/assignments', { params })
}

export function assignExpert(data: { projectId: number; expertId: number; stage: string; deadline?: string }) {
  return request.post('/reviews/assignments', data)
}

export function getMyReviewTasks() {
  return request.get('/reviews/my-tasks')
}

export function getPendingProjects(params: any) {
  return request.get('/reviews/pending-projects', { params })
}

export function getReviewOverview() {
  return request.get('/reviews/overview')
}

export function deleteAssignment(id: number) {
  return request.delete(`/reviews/assignments/${id}`)
}
