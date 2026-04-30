import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { logout as logoutApi } from '@/api/auth'

/**
 * 解析JWT token获取payload，不依赖localStorage存储敏感信息
 */
function parseJwtPayload(token: string): any {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch {
    return null
  }
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')

  // 从JWT token中解析用户信息，而非localStorage，防止篡改
  const userInfo = computed(() => {
    if (!token.value) return null
    return parseJwtPayload(token.value)
  })

  const userId = computed(() => userInfo.value?.userId || 0)
  const username = computed(() => userInfo.value?.sub || '')
  const role = computed(() => userInfo.value?.role || '')
  const realName = ref(localStorage.getItem('realName') || '')
  const collegeId = computed(() => userInfo.value?.collegeId || 0)

  function setUser(data: { token: string; userId: number; username: string; realName: string; role: string; collegeId: number }) {
    token.value = data.token
    realName.value = data.realName
    // 仅存储token和realName到localStorage
    localStorage.setItem('token', data.token)
    localStorage.setItem('realName', data.realName)
  }

  async function logout() {
    // 调用后端登出API，将Token加入黑名单
    try {
      await logoutApi()
    } catch {
      // 即使API调用失败也继续清除本地状态
    }
    token.value = ''
    realName.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('realName')
  }

  return { token, userId, username, realName, role, collegeId, setUser, logout }
})
