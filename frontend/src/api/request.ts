import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 30000,
})

request.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      if (res.code === 409) {
        // 并发冲突：其他用户已修改数据
        ElMessage.warning(res.message || '数据已被其他用户修改，请刷新后重试')
      } else if (res.code === 403) {
        // 权限不足（来自@PreAuthorize或SecurityConfig的accessDeniedHandler）
        ElMessage.error(res.message || '权限不足')
      } else {
        ElMessage.error(res.message || '请求失败')
      }
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      // 未认证：Token无效或已过期
      ElMessage.error('登录已过期，请重新登录')
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
    } else if (status === 403) {
      // 无权限：Spring Security过滤器层直接拒绝
      ElMessage.error('权限不足，请联系管理员')
    } else {
      ElMessage.error(error.response?.data?.message || error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request
