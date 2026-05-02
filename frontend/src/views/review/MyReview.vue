<template>
  <div class="page-container">
    <div class="page-header">
      <h2>我的评审任务</h2>
      <div style="display:flex;align-items:center;gap:12px">
        <el-tag type="info">共 {{ tasks.length }} 项，待处理 {{ pendingCount }} 项</el-tag>
        <el-button v-if="pendingCount > 0" type="primary" @click="goToNextPending">
          <el-icon><Right /></el-icon>
          下一个待处理
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" style="margin-bottom: 16px">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane name="pending">
        <template #label>
          待处理 <el-badge v-if="pendingCount > 0" :value="pendingCount" class="tab-badge" />
        </template>
      </el-tab-pane>
      <el-tab-pane label="已完成" name="scored" />
      <el-tab-pane label="评审打分" name="review" />
      <el-tab-pane label="审核任务" name="audit" />
    </el-tabs>

    <div v-loading="loading">
      <el-empty v-if="!loading && filteredTasks.length === 0" :description="emptyText" />

      <div class="task-grid">
        <div
          v-for="task in filteredTasks"
          :key="taskKey(task)"
          class="task-card"
          :class="{
            'task-scored': task.scored,
            'task-overdue': task.type === 'review' && isOverdue(task.deadline) && !task.scored,
            'task-audit': task.type === 'audit'
          }"
        >
          <div class="task-card-header">
            <span class="task-project-name">{{ task.projectName || '项目 #' + task.projectId }}</span>
            <el-tag
              :type="task.type === 'audit' ? 'danger' : task.stage === 'college' ? 'primary' : 'warning'"
              size="small"
              effect="plain"
            >
              {{ task.type === 'audit' ? auditTypeLabel(task.auditType) : (task.stage === 'college' ? '院级评审' : '校级评审') }}
            </el-tag>
          </div>

          <div class="task-card-body">
            <div class="task-info-row">
              <span class="task-label">任务类型</span>
              <el-tag :type="task.type === 'audit' ? 'danger' : 'primary'" size="small">
                {{ task.type === 'audit' ? '审核' : '评审打分' }}
              </el-tag>
            </div>
            <div class="task-info-row">
              <span class="task-label">处理状态</span>
              <el-tag :type="task.scored ? 'success' : 'danger'" size="small">
                {{ task.scored ? '已完成' : '待处理' }}
              </el-tag>
            </div>
            <div class="task-info-row">
              <span class="task-label">项目状态</span>
              <el-tag :type="getProjectStatusType(task.projectStatus)" size="small">
                {{ getProjectStatusText(task.projectStatus) }}
              </el-tag>
            </div>
            <div v-if="task.type === 'review' && task.assignTime" class="task-info-row">
              <span class="task-label">分配时间</span>
              <span class="task-value">{{ formatTime(task.assignTime) }}</span>
            </div>
            <div v-if="task.type === 'review' && task.deadline" class="task-info-row">
              <span class="task-label">截止时间</span>
              <span :class="['task-value', { 'deadline-overdue': isOverdue(task.deadline) && !task.scored, 'deadline-warn': isNearDeadline(task.deadline) && !task.scored }]">
                {{ formatTime(task.deadline) }}
                <el-tag v-if="isOverdue(task.deadline) && !task.scored" type="danger" size="small" style="margin-left:4px">已超时</el-tag>
                <el-tag v-else-if="isNearDeadline(task.deadline) && !task.scored" type="warning" size="small" style="margin-left:4px">即将到期</el-tag>
              </span>
            </div>
          </div>

          <div class="task-card-footer">
            <!-- 评审打分任务 -->
            <template v-if="task.type === 'review'">
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
            </template>

            <!-- 审核任务 -->
            <template v-if="task.type === 'audit' && !task.scored">
              <el-button
                type="success"
                size="small"
                @click="handleAudit(task, 'pass')"
              >
                <el-icon><CircleCheck /></el-icon>
                通过
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleAudit(task, 'reject')"
              >
                <el-icon><CircleClose /></el-icon>
                驳回
              </el-button>
            </template>
            <template v-if="task.type === 'audit' && task.scored">
              <el-button type="success" size="small" plain disabled>
                <el-icon><CircleCheck /></el-icon>
                已完成
              </el-button>
            </template>

            <el-button size="small" @click="goToProjectDetail(task.projectId)">
              <el-icon><View /></el-icon>
              查看项目
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 审核意见弹窗 -->
    <el-dialog v-model="auditDialogVisible" :title="auditDialogTitle" width="480px">
      <el-form label-width="80px">
        <el-form-item label="审核结果">
          <el-tag :type="auditResult === 'pass' ? 'success' : 'danger'" size="large">
            {{ auditResult === 'pass' ? '通过' : '驳回' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="auditOpinion" type="textarea" :rows="3" placeholder="请输入审核意见（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="auditSubmitting" @click="confirmAudit">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyReviewTasks } from '@/api/review'
import { teacherAudit, collegeAudit, schoolAudit } from '@/api/project'
import { Edit, CircleCheck, CircleClose, View, Right } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const tasks = ref<any[]>([])
const activeTab = ref('all')

// 审核弹窗状态
const auditDialogVisible = ref(false)
const auditDialogTitle = ref('')
const auditResult = ref('')
const auditOpinion = ref('')
const auditSubmitting = ref(false)
const currentAuditTask = ref<any>(null)

const pendingCount = computed(() => tasks.value.filter(t => !t.scored).length)

const filteredTasks = computed(() => {
  if (activeTab.value === 'pending') return tasks.value.filter(t => !t.scored)
  if (activeTab.value === 'scored') return tasks.value.filter(t => t.scored)
  if (activeTab.value === 'review') return tasks.value.filter(t => t.type === 'review')
  if (activeTab.value === 'audit') return tasks.value.filter(t => t.type === 'audit')
  return tasks.value
})

const emptyText = computed(() => {
  if (activeTab.value === 'pending') return '暂无待处理任务'
  if (activeTab.value === 'scored') return '暂无已完成任务'
  if (activeTab.value === 'review') return '暂无评审打分任务'
  if (activeTab.value === 'audit') return '暂无审核任务'
  return '暂无任务'
})

const projectStatusMap: Record<string, string> = {
  draft: '草稿', wait_teacher_audit: '待导师审核', wait_college_assign: '待院级分配',
  wait_college_review: '待院级评审', wait_college_audit: '待院级终审',
  wait_school_assign: '待校级分配', wait_school_review: '待校级评审', wait_school_audit: '待校级终审',
  approved: '已立项', rejected: '已驳回', running: '运行中', mid_checking: '中期检查',
  conclude_apply: '待结题', concluded: '已结题'
}

function getProjectStatusText(s: string) { return projectStatusMap[s] || s || '-' }

function getProjectStatusType(s: string) {
  if (!s) return 'info'
  if (s === 'approved' || s === 'running' || s === 'concluded') return 'success'
  if (s === 'rejected') return 'danger'
  return 'warning'
}

function auditTypeLabel(t: string) {
  const map: Record<string, string> = {
    teacher_audit: '导师审核',
    college_audit: '院级终审',
    school_audit: '校级终审'
  }
  return map[t] || t
}

function taskKey(task: any) {
  return task.type === 'audit' ? `audit-${task.auditType}-${task.projectId}` : `review-${task.assignmentId}`
}

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
  const task = tasks.value.find(t => t.projectId === projectId && t.type === 'review' && !t.scored)
  if (task) {
    router.push({ path: `/reviews/score/${projectId}`, query: { assignmentId: String(task.assignmentId) } })
  }
}

function goToProjectDetail(projectId: number) {
  router.push(`/projects/${projectId}`)
}

function goToNextPending() {
  const pending = tasks.value.find(t => !t.scored)
  if (!pending) return
  if (pending.type === 'review') {
    goToScore(pending.projectId)
  } else {
    goToProjectDetail(pending.projectId)
  }
}

function handleAudit(task: any, result: string) {
  currentAuditTask.value = task
  auditResult.value = result
  auditOpinion.value = ''
  auditDialogTitle.value = `${auditTypeLabel(task.auditType)} - ${result === 'pass' ? '通过' : '驳回'}`
  auditDialogVisible.value = true
}

async function confirmAudit() {
  if (!currentAuditTask.value) return
  auditSubmitting.value = true
  try {
    const data = { projectId: currentAuditTask.value.projectId, result: auditResult.value, opinion: auditOpinion.value }
    const auditType = currentAuditTask.value.auditType
    if (auditType === 'teacher_audit') {
      await teacherAudit(data)
    } else if (auditType === 'college_audit') {
      await collegeAudit(data)
    } else if (auditType === 'school_audit') {
      await schoolAudit(data)
    }
    ElMessage.success(auditResult.value === 'pass' ? '审核通过' : '已驳回')
    auditDialogVisible.value = false
    loadTasks()
  } catch {} finally {
    auditSubmitting.value = false
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

  &.task-audit {
    border-left: 3px solid #e63946;
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
  flex-wrap: wrap;
}

.tab-badge {
  margin-left: 4px;
  :deep(.el-badge__content) {
    top: -2px;
  }
}
</style>
