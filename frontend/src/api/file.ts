import request from './request'

export function uploadFile(formData: FormData) {
  return request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getDownloadUrl(id: number) {
  return request.get(`/files/${id}/download`)
}
