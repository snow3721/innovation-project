<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo-wrap" @click="isCollapse = !isCollapse">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="28" height="28">
            <path d="M12 2L2 7l10 5 10-5-10-5z"/>
            <path d="M2 17l10 5 10-5"/>
            <path d="M2 12l10 5 10-5"/>
          </svg>
        </div>
        <transition name="fade">
          <span v-if="!isCollapse" class="logo-text">创新项目系统</span>
        </transition>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="true"
        background-color="#1a1a2e"
        text-color="#a0aec0"
        active-text-color="#4361ee"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>

        <el-sub-menu index="project-mgr">
          <template #title>
            <el-icon><Folder /></el-icon>
            <span>项目管理</span>
          </template>
          <el-menu-item index="/projects">项目列表</el-menu-item>
          <el-menu-item index="/projects/create">创建项目</el-menu-item>
        </el-sub-menu>

        <el-menu-item v-if="hasRole(['expert','teacher','college_admin','school_admin'])" index="/reviews/my-tasks">
          <el-icon><Edit /></el-icon>
          <template #title>我的评审任务</template>
        </el-menu-item>

        <el-menu-item v-if="hasRole(['school_admin','college_admin'])" index="/reviews">
          <el-icon><Edit /></el-icon>
          <template #title>评审管理</template>
        </el-menu-item>

        <el-menu-item index="/milestones">
          <el-icon><Flag /></el-icon>
          <template #title>里程碑</template>
        </el-menu-item>

        <el-sub-menu index="achievement-mgr">
          <template #title>
            <el-icon><Trophy /></el-icon>
            <span>成果管理</span>
          </template>
          <el-menu-item index="/achievements">成果列表</el-menu-item>
          <el-menu-item index="/achievements/create">提交成果</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/messages">
          <el-icon><Bell /></el-icon>
          <template #title>
            消息中心
            <el-badge v-if="unreadCount > 0" :value="unreadCount" :max="99" style="margin-left: 6px" />
          </template>
        </el-menu-item>
        <el-menu-item index="/chat">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>
            站内信
            <el-badge v-if="chatUnread > 0" :value="chatUnread" :max="99" style="margin-left: 6px" />
          </template>
        </el-menu-item>

        <el-menu-item v-if="hasRole(['school_admin','college_admin'])" index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>

        <el-menu-item v-if="hasRole(['school_admin','college_admin'])" index="/experts">
          <el-icon><Avatar /></el-icon>
          <template #title>专家管理</template>
        </el-menu-item>

        <el-menu-item v-if="hasRole(['school_admin'])" index="/colleges">
          <el-icon><School /></el-icon>
          <template #title>学院管理</template>
        </el-menu-item>

        <el-menu-item v-if="hasRole(['school_admin'])" index="/categories">
          <el-icon><Menu /></el-icon>
          <template #title>类别管理</template>
        </el-menu-item>

        <el-menu-item v-if="hasRole(['school_admin','college_admin'])" index="/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据统计</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" class="msg-badge">
            <el-button :icon="Bell" circle size="small" @click="$router.push('/messages')" />
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" class="user-avatar">{{ userStore.realName?.charAt(0) }}</el-avatar>
              <span class="user-name">{{ userStore.realName }}</span>
              <el-tag size="small" type="primary" effect="plain">{{ roleText }}</el-tag>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUnreadCount } from '@/api/message'
import { getConversations } from '@/api/chat'
import { useChatStore } from '@/stores/chat'
import {
  Odometer, Folder, Edit, Flag, Trophy, User, Avatar,
  School, Menu, DataAnalysis, Bell, ChatDotRound
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const unreadCount = ref(0)
const chatStore = useChatStore()
const chatUnread = computed(() => chatStore.totalUnread)
let timer: any = null

async function fetchUnreadCount() {
  try {
    const res: any = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch {}
}

async function fetchChatConversations() {
  try {
    const res: any = await getConversations()
    chatStore.setConversations(res.data || [])
  } catch {}
}

onMounted(() => {
  fetchUnreadCount()
  fetchChatConversations()
  timer = setInterval(() => {
    fetchUnreadCount()
    fetchChatConversations()
  }, 30000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => (route.meta.title as string) || '')

const roleText = computed(() => {
  const map: Record<string, string> = {
    student: '学生',
    teacher: '导师',
    college_admin: '院管理员',
    school_admin: '校管理员',
    expert: '评审专家'
  }
  return map[userStore.role] || userStore.role
})

function hasRole(roles: string[]) {
  return roles.includes(userStore.role)
}

function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (cmd === 'profile') {
    router.push('/profile')
  }
}
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
}

.sidebar {
  background: #1a1a2e;
  transition: width 0.3s;
  overflow-x: hidden;

  .logo-wrap {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    cursor: pointer;
    border-bottom: 1px solid rgba(255,255,255,0.06);
    padding: 0 16px;

    .logo-icon {
      color: #4361ee;
      flex-shrink: 0;
    }

    .logo-text {
      font-size: 16px;
      font-weight: 700;
      color: #fff;
      white-space: nowrap;
    }
  }

  :deep(.el-menu) {
    border-right: none;
  }

  :deep(.el-menu-item.is-active) {
    background: rgba(67, 97, 238, 0.15) !important;
    border-right: 3px solid #4361ee;
  }

  :deep(.el-sub-menu__title:hover),
  :deep(.el-menu-item:hover) {
    background: rgba(255,255,255,0.05) !important;
  }
}

.main-container {
  background: var(--bg-page);
}

.header {
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: var(--shadow-sm);
  z-index: 10;

  .header-left {
    display: flex;
    align-items: center;
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .msg-badge {
    :deep(.el-badge__content) {
      top: -2px;
    }
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;

    .user-avatar {
      background: var(--primary);
      color: #fff;
      font-weight: 600;
    }

    .user-name {
      font-size: 14px;
      font-weight: 500;
      color: var(--text-primary);
    }
  }
}

.main-content {
  padding: 20px;
  overflow-y: auto;
  background: var(--bg-page);
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
