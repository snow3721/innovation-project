<template>
  <div class="page-container">
    <div class="page-header">
      <h2>项目详情</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div v-if="forbidden" class="forbidden-tip">
      <el-empty description="无权查看该项目">
        <el-button type="primary" @click="$router.back()">返回</el-button>
      </el-empty>
    </div>

    <div v-else-if="project" class="detail-content">
      <el-row :gutter="16">
        <el-col :span="16">
          <el-card shadow="never">
            <template #header>
              <div style="display:flex;justify-content:space-between;align-items:center">
                <span style="font-weight:600;font-size:18px">{{ project.projectName }}</span>
                <span :class="['status-tag', project.status]">{{ getStatusText(project.status) }}</span>
              </div>
            </template>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="项目类别">{{ project.catName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="所属学院">{{ project.collegeName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="负责人">{{ project.leaderName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="指导老师">{{ project.teacherName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="申请经费">{{ project.totalBudget?.toLocaleString() || '-' }} 元</el-descriptions-item>
              <el-descriptions-item label="申报时间">{{ project.applyTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="项目周期">{{ project.startTime || '-' }} 至 {{ project.endTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="申报年份">{{ project.applyYear || '-' }}</el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- 中期检查 & 结题信息 -->
          <el-card v-if="midCheckInfo || concludeInfo" shadow="never" style="margin-top: 16px">
            <template #header><span style="font-weight:600">流程记录</span></template>
            <el-descriptions :column="2" border>
              <el-descriptions-item v-if="midCheckInfo" label="中期检查">
                <el-tag :type="midCheckInfo.status === 'pass' ? 'success' : midCheckInfo.status === 'reject' ? 'danger' : 'warning'" size="small">
                  {{ midCheckStatusText(midCheckInfo.status) }}
                </el-tag>
                <span style="margin-left: 8px; color: #999">{{ midCheckInfo.submitTime }}</span>
              </el-descriptions-item>
              <el-descriptions-item v-if="concludeInfo" label="结题验收">
                <el-tag :type="concludeInfo.status === 'pass' ? 'success' : concludeInfo.status === 'reject' ? 'danger' : 'warning'" size="small">
                  {{ concludeStatusText(concludeInfo.status) }}
                </el-tag>
                <span style="margin-left: 8px; color: #999">{{ concludeInfo.submitTime }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card shadow="never" style="margin-bottom: 16px">
            <template #header><span style="font-weight:600">项目状态</span></template>
            <el-steps :active="getStepIndex()" direction="vertical" :space="40" finish-status="success">
              <el-step title="草稿" />
              <el-step title="导师审核" />
              <el-step title="院级分配" />
              <el-step title="院级评审" />
              <el-step title="院级终审" />
              <el-step title="校级分配" />
              <el-step title="校级评审" />
              <el-step title="校级终审" />
              <el-step title="已立项" />
              <el-step title="运行中" />
              <el-step title="中期检查" />
              <el-step title="结题验收" />
            </el-steps>
          </el-card>

          <el-card shadow="never">
            <template #header><span style="font-weight:600">操作</span></template>
            <div style="display:flex;flex-direction:column;gap:8px">
              <el-button v-if="(project.status === 'draft' || project.status === 'rejected') && isOwner" type="primary" @click="handleSubmit">提交审核</el-button>
              <el-button v-if="(project.status === 'running' || project.status === 'mid_checking') && isOwner" type="warning" @click="handleMidCheck">提交中期检查</el-button>
              <el-button v-if="(project.status === 'running' || project.status === 'mid_checking') && isOwner" type="success" @click="handleConclude">提交结题申请</el-button>
              <el-button v-if="userStore.role !== 'student'" @click="$router.push('/reviews/my-tasks')">
                <el-icon><List /></el-icon>
                我的评审任务
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 中期检查审核对话框 -->
    <el-dialog v-model="midCheckDialogVisible" title="审核中期检查" width="500px">
      <el-form label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.result">
            <el-radio label="pass">通过</el-radio>
            <el-radio label="reject">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="auditForm.opinion" type="textarea" :rows="3" placeholder="请输入审核意见（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="midCheckDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMidCheckAudit">确认</el-button>
      </template>
    </el-dialog>

    <!-- 结题审核对话框 -->
    <el-dialog v-model="concludeDialogVisible" title="审核结题申请" width="500px">
      <el-form label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.result">
            <el-radio label="pass">通过</el-radio>
            <el-radio label="reject">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="auditForm.opinion" type="textarea" :rows="3" placeholder="请输入审核意见（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="concludeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitConcludeAudit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProject, submitProject, submitMidCheck, submitConclude, auditMidCheck, auditConclude, getMidChecks, getConcludes } from '@/api/project'
import { useUserStore } from '@/stores/user'
import { List } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const project = ref<any>(null)
const forbidden = ref(false)
const isOwner = computed(() => project.value?.leaderId === userStore.userId)

const midCheckInfo = ref<any>(null)
const concludeInfo = ref<any>(null)
const midCheckDialogVisible = ref(false)
const concludeDialogVisible = ref(false)
const auditForm = ref({ result: 'pass', opinion: '' })
const currentAuditId = ref<number>(0)

const statusMap: Record<string, string> = {
  draft: '草稿', wait_teacher_audit: '待导师审核', wait_college_assign: '待院级分配',
  wait_college_review: '待院级评审', wait_college_audit: '待院级终审',
  wait_school_assign: '待校级分配', wait_school_review: '待校级评审', wait_school_audit: '待校级终审',
  approved: '已立项', rejected: '已驳回', running: '运行中', mid_checking: '中期检查',
  conclude_apply: '待结题', concluded: '已结题'
}

function getStatusText(s: string) { return statusMap[s] || s }
function midCheckStatusText(s: string) {
  const map: Record<string, string> = { waiting: '待审核', pass: '已通过', reject: '已驳回' }
  return map[s] || s
}
function concludeStatusText(s: string) {
  const map: Record<string, string> = { waiting: '待审核', pass: '已通过', reject: '已驳回' }
  return map[s] || s
}
function getStepIndex() {
  const s = project.value?.status
  const map: Record<string, number> = {
    draft: 0, wait_teacher_audit: 1, wait_college_assign: 2, wait_college_review: 3,
    wait_college_audit: 4, wait_school_assign: 5, wait_school_review: 6,
    wait_school_audit: 7, approved: 8, running: 9, mid_checking: 10, conclude_apply: 11, concluded: 12
  }
  return map[s] ?? (s === 'rejected' ? 0 : 12)
}

async function loadData() {
  const id = Number(route.params.id)
  try {
    const res: any = await getProject(id)
    project.value = res.data
    // 加载中期检查和结题信息
    loadMidCheckInfo(id)
    loadConcludeInfo(id)
  } catch {
    forbidden.value = true
  }
}

async function loadMidCheckInfo(projectId: number) {
  try {
    const res: any = await getMidChecks({ page: 1, size: 1, projectId })
    if (res.data?.records?.length > 0) {
      midCheckInfo.value = res.data.records[0]
    }
  } catch { /* ignore */ }
}

async function loadConcludeInfo(projectId: number) {
  try {
    const res: any = await getConcludes({ page: 1, size: 1, projectId })
    if (res.data?.records?.length > 0) {
      concludeInfo.value = res.data.records[0]
    }
  } catch { /* ignore */ }
}

async function handleSubmit() {
  await submitProject(project.value.projectId)
  ElMessage.success('已提交审核')
  loadData()
}

async function handleMidCheck() {
  await ElMessageBox.confirm('确认提交中期检查报告？', '提示', { type: 'warning' })
  await submitMidCheck(project.value.projectId)
  ElMessage.success('中期检查报告已提交')
  loadData()
}

async function handleConclude() {
  await ElMessageBox.confirm('确认提交结题申请？', '提示', { type: 'warning' })
  await submitConclude(project.value.projectId)
  ElMessage.success('结题申请已提交')
  loadData()
}

function showMidCheckAudit(midId: number) {
  currentAuditId.value = midId
  auditForm.value = { result: 'pass', opinion: '' }
  midCheckDialogVisible.value = true
}

async function submitMidCheckAudit() {
  await auditMidCheck(currentAuditId.value, auditForm.value)
  ElMessage.success('审核完成')
  midCheckDialogVisible.value = false
  loadData()
}

function showConcludeAudit(concludeId: number) {
  currentAuditId.value = concludeId
  auditForm.value = { result: 'pass', opinion: '' }
  concludeDialogVisible.value = true
}

async function submitConcludeAudit() {
  await auditConclude(currentAuditId.value, auditForm.value)
  ElMessage.success('审核完成')
  concludeDialogVisible.value = false
  loadData()
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.forbidden-tip { padding: 80px 0; text-align: center; }
</style>
