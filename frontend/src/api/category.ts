import request from './request'

export function getCategories() {
  return request.get('/categories')
}

export function createCategory(data: any) {
  return request.post('/categories', data)
}

export function updateCategory(id: number, data: any) {
  return request.put(`/categories/${id}`, data)
}

export function deleteCategory(id: number) {
  return request.delete(`/categories/${id}`)
}
