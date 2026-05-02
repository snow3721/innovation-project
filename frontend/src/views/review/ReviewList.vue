<template>
  <div class="review-page">
    <div class="page-header">
      <h2>评审管理</h2>
      <p class="page-desc">项目管理与专家评审分配中心</p>
    </div>

    <!-- 概览统计卡片 -->
    <div class="overview-grid" v-loading="overviewLoading">
      <div class="stat-card college-assign" @click="switchTab('pending'); pendingFilter.stage='college'">
        <div class="stat-icon">
          <el-icon :size="24"><UserFilled /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ overview.collegeAssignCount || 0 }}</span>
          <span class="stat-label">待院级分配</span>
        </div>
      </div>
      <div class="stat-card college-review">
        <div class="stat-icon">
          <el-icon :size="24"><EditPen /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ overview.collegeReviewCount || 0 }}</span>
          <span class="stat-label">院级评审中</span>
        </div>
      </div>
      <div class="stat-card college-audit">
        <div class="stat-icon">
          <el-icon :size="24"><Checked /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ overview.collegeAuditCount || 0 }}</span>
          <span class="stat-label">待院级终审</span>
        </div>
      </div>
      <div class="stat-card school-assign" @click="switchTab('pending'); pendingFilter.stage='school'">
        <div class="stat-icon">
          <el-icon :size="24"><UserFilled /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ overview.schoolAssignCount || 0 }}</span>
          <span class="stat-label">待校级分配</span>
        </div>
      </div>
      <div class="stat-card school-review">
        <div class="stat-icon">
          <el-icon :size="24"><EditPen /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ overview.schoolReviewCount || 0 }}</span>
          <span class="stat-label">校级评审中</span>
        </div>
      </div>
      <div class="stat-card school-audit">
        <div class="stat-icon">
          <el-icon :size="24"><Checked /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ overview.schoolAuditCount || 0 }}</span>
          <span class="stat-label">待校级终审</span>
        </div>
      </div>
      <div class="stat-card approved">
        <div class="stat-icon">
          <el-icon :size="24"><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ overview.approvedCount || 0 }}</span>
          <span class="stat-label">已立项</span>
        </div>
      </div>
      <div class="stat-card rejected">
        <div class="stat-icon">
          <el-icon :size="24"><CircleClose /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ overview.rejectedCount || 0 }}</span>
          <span class="stat-label">已驳回</span>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <el-tabs v-model="activeTab" class="main-tabs">
      <!-- Tab 1: 待分配项目 -->
      <el-tab-pane name="pending">
        <template #label>
          <span>待分配项目</span>
          <el-badge v-if="(overview.collegeAssignCount || 0) + (overview.schoolAssignCount || 0) > 0"
            :value="(overview.collegeAssignCount || 0) + (overview.schoolAssignCount || 0)"
            class="tab-badge" />
        </template>

        <div class="filter-bar">
          <el-select v-model="pendingFilter.stage" placeholder="分配阶段" clearable style="width:140px" @change="loadPendingProjects">
            <el-option label="院级待分配" value="college" />
            <el-option label="校级待分配" value="school" />
          </el-select>
          <el-input v-model="pendingFilter.projectName" placeholder="项目名称" clearable style="width:200px" @clear="loadPendingProjects" @keyup.enter="loadPendingProjects" />
          <el-button type="primary" @click="loadPendingProjects">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>

        <el-table :data="pendingData" stripe v-loading="pendingLoading" empty-text="暂无待分配项目">
          <el-table-column prop="projectId" label="ID" width="60" />
          <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
          <el-table-column prop="catName" label="类别" width="100" />
          <el-table-column prop="leaderName" label="负责人" width="80" />
          <el-table-column prop="teacherName" label="指导老师" width="90" />
          <el-table-column prop="collegeName" label="学院" width="120" show-overflow-tooltip />
          <el-table-column prop="statusText" label="状态" width="110">
            <template #default="{ row }">
              <el-tag type="warning" size="small">{{ row.statusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="applyTime" label="申报时间" width="160">
            <template #default="{ row }">{{ formatTime(row.applyTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="openAssignDialog(row)">
                <el-icon><Plus /></el-icon>
                分配专家
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination v-model:current-page="pendingPage" v-model:page-size="pendingSize"
            :total="pendingTotal" layout="total, sizes, prev, pager, next"
            @size-change="loadPendingProjects" @current-change="loadPendingProjects" />
        </div>
      </el-tab-pane>

      <!-- Tab 2: 专家分配记录 -->
      <el-tab-pane label="分配记录" name="assignments">
        <div class="filter-bar">
          <el-select v-model="assignFilter.stage" placeholder="评审阶段" clearable style="width:140px" @change="loadAssignments">
            <el-option label="院级评审" value="college" />
            <el-option label="校级评审" value="school" />
          </el-select>
          <el-input v-model="assignFilter.projectId" placeholder="项目ID" clearable style="width:120px" @clear="loadAssignments" @keyup.enter="loadAssignments" />
          <el-button type="primary" @click="loadAssignments">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>

        <el-table :data="assignmentData" stripe v-loading="assignLoading" empty-text="暂无分配记录">
          <el-table-column prop="assignmentId" label="ID" width="60" />
          <el-table-column prop="projectId" label="项目ID" width="70" />
          <el-table-column prop="projectName" label="项目名称" min-width="160" show-overflow-tooltip />
          <el-table-column prop="expertName" label="专家姓名" width="100" />
          <el-table-column prop="expertField" label="研究方向" width="140" show-overflow-tooltip />
          <el-table-column prop="stage" label="评审阶段" width="100">
            <template #default="{ row }">
              <el-tag :type="row.stage === 'college' ? 'primary' : 'warning'" size="small" effect="plain">
                {{ row.stage === 'college' ? '院级' : '校级' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="assignTime" label="分配时间" width="160">
            <template #default="{ row }">{{ formatTime(row.assignTime) }}</template>
          </el-table-column>
          <el-table-column prop="deadline" label="截止时间" width="160">
            <template #default="{ row }">
              <span v-if="row.deadline">{{ row.deadline }}</span>
              <el-tag v-else type="info" size="small">未设置</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination v-model:current-page="assignPage" v-model:page-size="assignSize"
            :total="assignTotal" layout="total, sizes, prev, pager, next"
            @size-change="loadAssignments" @current-change="loadAssignments" />
        </div>
      </el-tab-pane>

      <!-- Tab 3: 评审成绩 -->
      <el-tab-pane label="评审成绩" name="scores">
        <div class="filter-bar">
          <el-select v-model="scoreFilter.stage" placeholder="评审阶段" clearable style="width:140px" @change="loadScores">
            <el-option label="院级评审" value="college" />
            <el-option label="校级评审" value="school" />
          </el-select>
          <el-input v-model="scoreFilter.projectId" placeholder="项目ID" clearable style="width:120px" @clear="loadScores" @keyup.enter="loadScores" />
          <el-button type="primary" @click="loadScores">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>

        <el-table :data="scoreData" stripe v-loading="scoreLoading" empty-text="暂无评审成绩">
          <el-table-column prop="scoreId" label="ID" width="60" />
          <el-table-column prop="projectId" label="项目ID" width="70" sortable />
          <el-table-column prop="projectName" label="项目名称" min-width="160" show-overflow-tooltip />
          <el-table-column prop="expertName" label="评审专家" width="100" />
          <el-table-column prop="reviewStage" label="评审阶段" width="100">
            <template #default="{ row }">
              <el-tag :type="row.reviewStage === 'college' ? 'primary' : 'warning'" size="small" effect="plain">
                {{ row.reviewStage === 'college' ? '院级' : '校级' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="scoreInnovation" label="创新性" width="80" sortable />
          <el-table-column prop="scoreFeasibility" label="可行性" width="80" sortable />
          <el-table-column prop="scoreTeam" label="团队" width="80" sortable />
          <el-table-column prop="scoreValue" label="价值" width="80" sortable />
          <el-table-column prop="totalScore" label="总分" width="80" sortable>
            <template #default="{ row }">
              <el-tag :type="row.totalScore >= 80 ? 'success' : row.totalScore >= 60 ? 'warning' : 'danger'">
                {{ row.totalScore }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="scoreTime" label="评审时间" width="160">
            <template #default="{ row }">{{ formatTime(row.scoreTime) }}</template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrap">
          <el-pagination v-model:current-page="scorePage" v-model:page-size="scoreSize"
            :total="scoreTotal" layout="total, sizes, prev, pager, next"
            @size-change="loadScores" @current-change="loadScores" />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 分配专家弹窗 -->
    <el-dialog v-model="assignDialogVisible" title="分配评审专家" width="560px" destroy-on-close>
      <div class="assign-project-info">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="项目名称">{{ assignTarget.projectName }}</el-descriptions-item>
          <el-descriptions-item label="项目类别">{{ assignTarget.catName }}</el-descriptions-item>
          <el-descriptions-item label="负责人">{{ assignTarget.leaderName }}</el-descriptions-item>
          <el-descriptions-item label="指导老师">{{ assignTarget.teacherName }}</el-descriptions-item>
          <el-descriptions-item label="学院">{{ assignTarget.collegeName }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag type="warning" size="small">{{ assignTarget.statusText }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <el-form :model="assignForm" label-width="90px" style="margin-top: 20px">
        <el-form-item label="评审阶段">
          <el-select v-model="assignForm.stage" style="width:100%" disabled>
            <el-option label="院级评审" value="college" />
            <el-option label="校级评审" value="school" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择专家">
          <el-select v-model="assignForm.expertId" placeholder="请选择专家" filterable style="width:100%">
            <el-option v-for="e in experts" :key="e.expertId"
              :label="`${e.realName}（${e.title || '无职称'} · ${e.researchField || '未填写'}）`"
              :value="e.expertId" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker v-model="assignForm.deadline" type="datetime"
            placeholder="设置评审截止时间" style="width:100%"
            value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="assignSubmitting" @click="handleAssign">
          <el-icon><Check /></el-icon>
          确认分配
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  UserFilled, EditPen, Checked, CircleCheck, CircleClose,
  Search, Plus, Check
} from '@element-plus/icons-vue'
import {
  getReviewScores, getAssignments, assignExpert,
  getPendingProjects, getReviewOverview
} from '@/api/review'
import { getExperts } from '@/api/expert'
import dayjs from 'dayjs'

// ===== 概览统计 =====
const overviewLoading = ref(false)
const overview = ref<Record<string, number>>({})

async function loadOverview() {
  overviewLoading.value = true
  try {
    const res: any = await getReviewOverview()
    overview.value = res.data || {}
  } catch {} finally { overviewLoading.value = false }
}

// ===== Tab 控制 =====
const activeTab = ref('pending')

function switchTab(tab: string) {
  activeTab.value = tab
}

// ===== 待分配项目 =====
const pendingLoading = ref(false)
const pendingData = ref<any[]>([])
const pendingPage = ref(1), pendingSize = ref(10), pendingTotal = ref(0)
const pendingFilter = reactive({ stage: '', projectName: '' })

async function loadPendingProjects() {
  pendingLoading.value = true
  try {
    const res: any = await getPendingProjects({
      page: pendingPage.value, size: pendingSize.value,
      ...pendingFilter
    })
    pendingData.value = res.data?.list || []
    pendingTotal.value = res.data?.total || 0
  } catch {} finally { pendingLoading.value = false }
}

// ===== 专家分配记录 =====
const assignLoading = ref(false)
const assignmentData = ref<any[]>([])
const assignPage = ref(1), assignSize = ref(10), assignTotal = ref(0)
const assignFilter = reactive({ stage: '', projectId: '' })

async function loadAssignments() {
  assignLoading.value = true
  try {
    const res: any = await getAssignments({
      page: assignPage.value, size: assignSize.value,
      stage: assignFilter.stage || undefined,
      projectId: assignFilter.projectId ? Number(assignFilter.projectId) : undefined
    })
    assignmentData.value = res.data?.list || []
    assignTotal.value = res.data?.total || 0
  } catch {} finally { assignLoading.value = false }
}

// ===== 评审成绩 =====
const scoreLoading = ref(false)
const scoreData = ref<any[]>([])
const scorePage = ref(1), scoreSize = ref(10), scoreTotal = ref(0)
const scoreFilter = reactive({ stage: '', projectId: '' })

async function loadScores() {
  scoreLoading.value = true
  try {
    const res: any = await getReviewScores({
      page: scorePage.value, size: scoreSize.value,
      stage: scoreFilter.stage || undefined,
      projectId: scoreFilter.projectId ? Number(scoreFilter.projectId) : undefined
    })
    scoreData.value = res.data?.list || []
    scoreTotal.value = res.data?.total || 0
  } catch {} finally { scoreLoading.value = false }
}

// ===== 分配专家弹窗 =====
const assignDialogVisible = ref(false)
const assignSubmitting = ref(false)
const experts = ref<any[]>([])
const assignTarget = ref<any>({})
const assignForm = reactive({ projectId: 0, expertId: undefined as number | undefined, stage: 'college', deadline: '' as string })

async function openAssignDialog(project: any) {
  assignTarget.value = project
  assignForm.projectId = project.projectId
  assignForm.expertId = undefined
  assignForm.deadline = ''
  // 根据项目状态自动设定评审阶段
  if (project.status === 'wait_college_assign') {
    assignForm.stage = 'college'
  } else if (project.status === 'wait_school_assign') {
    assignForm.stage = 'school'
  }
  assignDialogVisible.value = true
  // 加载专家列表
  if (experts.value.length === 0) {
    try {
      const res: any = await getExperts({ page: 1, size: 200 })
      experts.value = res.data?.list || []
    } catch {}
  }
}

async function handleAssign() {
  if (!assignForm.expertId) {
    ElMessage.warning('请选择评审专家')
    return
  }
  assignSubmitting.value = true
  try {
    await assignExpert({
      projectId: assignForm.projectId,
      expertId: assignForm.expertId,
      stage: assignForm.stage,
      deadline: assignForm.deadline || undefined
    })
    ElMessage.success('专家分配成功')
    assignDialogVisible.value = false
    loadAssignments()
    loadPendingProjects()
    loadOverview()
  } catch {} finally { assignSubmitting.value = false }
}

// ===== 工具方法 =====
function formatTime(time: string) {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// ===== 初始化 =====
onMounted(() => {
  loadOverview()
  loadPendingProjects()
  loadAssignments()
  loadScores()
})
</script>

<style lang="scss" scoped>
.review-page {
  max-width: 1400px;
}

.page-header {
  margin-bottom: 20px;
  h2 { margin: 0 0 4px; font-size: 22px; font-weight: 700; color: #1a1a2e; }
  .page-desc { margin: 0; font-size: 14px; color: #9ca3af; }
}

/* 概览统计卡片 */
.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  border-radius: 10px;
  background: #fff;
  border: 1px solid #e5e7eb;
  cursor: default;
  transition: all 0.25s;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  }

  &.college-assign, &.school-assign {
    cursor: pointer;
    .stat-icon { background: #eff6ff; color: #3b82f6; }
  }
  &.college-review, &.school-review {
    .stat-icon { background: #fefce8; color: #ca8a04; }
  }
  &.college-audit, &.school-audit {
    .stat-icon { background: #faf5ff; color: #9333ea; }
  }
  &.approved {
    .stat-icon { background: #ecfdf5; color: #059669; }
  }
  &.rejected {
    .stat-icon { background: #fef2f2; color: #dc2626; }
  }
}

.stat-icon {
  width: 48px; height: 48px;
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  display: flex; flex-direction: column; gap: 2px;
  .stat-value { font-size: 24px; font-weight: 700; color: #1a1a2e; line-height: 1.2; }
  .stat-label { font-size: 13px; color: #6b7280; }
}

/* 主 Tabs */
.main-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 16px;
  }
  :deep(.el-tabs__item) {
    font-size: 15px; font-weight: 600;
  }
}

.tab-badge {
  margin-left: 6px;
  :deep(.el-badge__content) { top: 2px; }
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  align-items: center;
}

/* 分页 */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

/* 分配弹窗中的项目信息 */
.assign-project-info {
  background: #f8fafc;
  border-radius: 8px;
  padding: 12px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .overview-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .overview-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
