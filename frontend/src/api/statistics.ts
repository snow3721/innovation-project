import request from './request'

export function getOverview() {
  return request.get('/statistics/overview')
}

export function getByYear(year?: number) {
  return request.get('/statistics/by-year', { params: { year } })
}

export function getByCollege(collegeId?: number) {
  return request.get('/statistics/by-college', { params: { collegeId } })
}

export function getByCategory() {
  return request.get('/statistics/by-category')
}

export function exportExcel() {
  return request.get('/statistics/export', { responseType: 'blob' })
}
