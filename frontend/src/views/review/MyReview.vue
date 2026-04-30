<template>
  <div class="page-container">
    <div class="page-header">
      <h2>我的评审任务</h2>
      <div style="display:flex;align-items:center;gap:12px">
        <el-tag type="info">共 {{ tasks.length }} 项任务，待评审 {{ pendingCount }} 项</el-tag>
        <el-button v-if="pendingCount > 0" type="primary" @click="goToNextPending">
          <el-icon><Right /></el-icon>
          下一个待评审
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" style="margin-bottom: 16px">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane name="pending">
        <template #label>
          待评审 <el-badge v-if="pendingCount > 0" :value="pendingCount" class="tab-badge" />
        </template>
      </el-tab-pane>
      <el-tab-pane label="已评审" name="scored" />
    </el-tabs>

    <div v-loading="loading">
      <el-empty v-if="!loading && filteredTasks.length === 0" :description="activeTab === 'pending' ? '暂无待评审任务' : activeTab === 'scored' ? '暂无已评审任务' : '暂无评审任务'" />

      <div class="task-grid">
        <div
          v-for="task in filteredTasks"
          :key="task.assignmentId"
          class="task-card"
          :class="{ 'task-scored': task.scored, 'task-overdue': isOverdue(task.deadline) && !task.scored }"
        >
          <div class="task-card-header">
            <span class="task-project-name">{{ task.projectName || '项目 #' + task.projectId }}</span>
            <el-tag :type="task.stage === 'college' ? 'primary' : 'warning'" size="small" effect="plain">
              {{ task.stage === 'college' ? '院级评审' : '校级评审' }}
            </el-tag>
          </div>

          <div class="task-card-body">
            <div class="task-info-row">
              <span class="task-label">项目ID</span>
              <span class="task-value">{{ task.projectId }}</span>
            </div>
            <div class="task-info-row">
              <span class="task-label">评审状态</span>
              <el-tag :type="task.scored ? 'success' : 'danger'" size="small">
                {{ task.scored ? '已评审' : '待评审' }}
              </el-tag>
            </div>
            <div class="task-info-row">
              <span class="task-label">分配时间</span>
              <span class="task-value">{{ formatTime(task.assignTime) }}</span>
            </div>
            <div v-if="task.deadline" class="task-info-row">
              <span class="task-label">截止时间</span>
              <span :class="['task-value', { 'deadline-overdue': isOverdue(task.deadline) && !task.scored, 'deadline-warn': isNearDeadline(task.deadline) && !task.scored }]">
                {{ formatTime(task.deadline) }}
                <el-tag v-if="isOverdue(task.deadline) && !task.scored" type="danger" size="small" style="margin-left:4px">已超时</el-tag>
                <el-tag v-else-if="isNearDeadline(task.deadline) && !task.scored" type="warning" size="small" style="margin-left:4px">即将到期</el-tag>
              </span>
            </div>
          </div>

          <div class="task-card-footer">
            <el-button
              v-if="!task.scored"
              type="primary"
              size="small"
              @click="goToScore(task.projectId)"
            >
              <el-icon><Edit /></el-icon>
              评审打分
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              plain
              disabled
            >
              <el-icon><CircleCheck /></el-icon>
              已完成
            </el-button>
            <el-button size="small" @click="goToProjectDetail(task.projectId)">
              <el-icon><View /></el-icon>
              查看项目
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyReviewTasks } from '@/api/review'
import { Edit, CircleCheck, View, Right } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const tasks = ref<any[]>([])
const activeTab = ref('all')

const pendingCount = computed(() => tasks.value.filter(t => !t.scored).length)

const filteredTasks = computed(() => {
  if (activeTab.value === 'pending') return tasks.value.filter(t => !t.scored)
  if (activeTab.value === 'scored') return tasks.value.filter(t => t.scored)
  return tasks.value
})

function isOverdue(deadline: string) {
  if (!deadline) return false
  return dayjs(deadline).isBefore(dayjs(), 'day')
}

function isNearDeadline(deadline: string) {
  if (!deadline) return false
  const diff = dayjs(deadline).diff(dayjs(), 'day')
  return diff >= 0 && diff <= 3
}

function formatTime(time: string) {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

async function loadTasks() {
  loading.value = true
  try {
    const res: any = await getMyReviewTasks()
    tasks.value = res.data || []
  } catch {} finally {
    loading.value = false
  }
}

function goToScore(projectId: number) {
  router.push(`/reviews/score/${projectId}`)
}

function goToProjectDetail(projectId: number) {
  router.push(`/projects/${projectId}`)
}

function goToNextPending() {
  const pending = tasks.value.find(t => !t.scored)
  if (pending) {
    goToScore(pending.projectId)
  }
}

onMounted(loadTasks)
</script>

<style lang="scss" scoped>
.task-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 16px;
}

.task-card {
  background: #fff;
  border-radius: var(--radius-md, 10px);
  padding: 20px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0,0,0,0.08));
  border: 1px solid #e5e7eb;
  transition: all 0.3s;

  &:hover {
    box-shadow: var(--shadow-md, 0 4px 12px rgba(0,0,0,0.12));
    transform: translateY(-2px);
  }

  &.task-scored {
    border-left: 3px solid #06d6a0;
  }

  &.task-overdue {
    border-left: 3px solid #ef476f;
  }
}

.task-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 8px;

  .task-project-name {
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary, #1a1a2e);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    flex: 1;
  }
}

.task-card-body {
  margin-bottom: 16px;

  .task-info-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 6px 0;

    .task-label {
      font-size: 13px;
      color: var(--text-secondary, #9ca3af);
    }

    .task-value {
      font-size: 13px;
      color: var(--text-primary, #1a1a2e);
      font-weight: 500;
      display: flex;
      align-items: center;
    }

    .deadline-overdue {
      color: #ef476f;
      font-weight: 600;
    }

    .deadline-warn {
      color: #ffd166;
      font-weight: 600;
    }
  }
}

.task-card-footer {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.tab-badge {
  margin-left: 4px;
  :deep(.el-badge__content) {
    top: -2px;
  }
}
</style>
