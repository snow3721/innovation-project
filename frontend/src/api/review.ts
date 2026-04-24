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

export function assignExpert(data: { projectId: number; expertId: number; stage: string }) {
  return request.post('/reviews/assignments', data)
}
