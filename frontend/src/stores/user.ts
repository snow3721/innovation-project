import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(Number(localStorage.getItem('userId')) || 0)
  const username = ref(localStorage.getItem('username') || '')
  const realName = ref(localStorage.getItem('realName') || '')
  const role = ref(localStorage.getItem('role') || '')
  const collegeId = ref(Number(localStorage.getItem('collegeId')) || 0)

  function setUser(data: { token: string; userId: number; username: string; realName: string; role: string; collegeId: number }) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    realName.value = data.realName
    role.value = data.role
    collegeId.value = data.collegeId
    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', String(data.userId))
    localStorage.setItem('username', data.username)
    localStorage.setItem('realName', data.realName)
    localStorage.setItem('role', data.role)
    localStorage.setItem('collegeId', String(data.collegeId))
  }

  function logout() {
    token.value = ''
    userId.value = 0
    username.value = ''
    realName.value = ''
    role.value = ''
    collegeId.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('realName')
    localStorage.removeItem('role')
    localStorage.removeItem('collegeId')
  }

  return { token, userId, username, realName, role, collegeId, setUser, logout }
})
