<template>
  <div class="user-selector">
    <el-input
      v-model="keyword"
      placeholder="搜索姓名或账号"
      clearable
      :prefix-icon="Search"
      @keyup.enter="loadUsers"
      @clear="loadUsers"
    />
    <div class="user-results">
      <el-empty v-if="users.length === 0" description="未找到用户" :image-size="50" />
      <div v-for="u in users" :key="u.userId" class="user-item" @click="$emit('select', u)">
        <el-avatar :size="32">{{ u.realName?.charAt(0) }}</el-avatar>
        <span class="user-name">{{ u.realName }}</span>
        <span class="user-role">{{ roleLabel(u.role) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { searchUsers } from '@/api/user'

const keyword = ref('')
const users = ref<any[]>([])

function roleLabel(role: string) {
  const map: Record<string, string> = {
    student: '学生',
    teacher: '导师',
    college_admin: '院管理员',
    school_admin: '校管理员',
    expert: '评审专家'
  }
  return map[role] || role
}

async function loadUsers() {
  try {
    const res: any = await searchUsers(keyword.value || undefined)
    users.value = res.data || []
  } catch {}
}

onMounted(loadUsers)
</script>

<style lang="scss" scoped>
.user-selector {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.user-results {
  max-height: 320px;
  overflow-y: auto;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;

  &:hover {
    background: var(--el-fill-color-light);
  }

  .user-name {
    flex: 1;
    font-size: 14px;
    color: var(--text-primary);
  }

  .user-role {
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
}
</style>