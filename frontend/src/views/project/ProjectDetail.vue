<template>
  <div class="page-container">
    <div class="page-header">
      <h2>项目详情</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div v-if="project" class="detail-content">
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
        </el-col>

        <el-col :span="8">
          <el-card shadow="never" style="margin-bottom: 16px">
            <template #header><span style="font-weight:600">项目状态</span></template>
            <el-steps :active="getStepIndex()" direction="vertical" :space="40" finish-status="success">
              <el-step title="草稿" />
              <el-step title="导师审核" />
              <el-step title="院级评审" />
              <el-step title="院级终审" />
              <el-step title="校级评审" />
              <el-step title="校级终审" />
              <el-step title="已立项" />
            </el-steps>
          </el-card>

          <el-card shadow="never">
            <template #header><span style="font-weight:600">操作</span></template>
            <div style="display:flex;flex-direction:column;gap:8px">
              <el-button v-if="project.status === 'draft'" type="primary" @click="handleSubmit">提交审核</el-button>
              <el-button v-if="project.status === 'wait_teacher_audit' && userStore.role === 'teacher'" type="success" @click="handleAudit('pass')">导师通过</el-button>
              <el-button v-if="project.status === 'wait_teacher_audit' && userStore.role === 'teacher'" type="danger" @click="handleAudit('reject')">导师驳回</el-button>
              <el-button v-if="project.status === 'wait_college_audit' && (userStore.role === 'college_admin')" type="success" @click="handleCollegeAudit('pass')">院级通过</el-button>
              <el-button v-if="project.status === 'wait_college_audit' && (userStore.role === 'college_admin')" type="danger" @click="handleCollegeAudit('reject')">院级驳回</el-button>
              <el-button v-if="project.status === 'wait_school_audit' && userStore.role === 'school_admin'" type="success" @click="handleSchoolAudit('pass')">校级通过</el-button>
              <el-button v-if="project.status === 'wait_school_audit' && userStore.role === 'school_admin'" type="danger" @click="handleSchoolAudit('reject')">校级驳回</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProject, submitProject, teacherAudit, collegeAudit, schoolAudit } from '@/api/project'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const project = ref<any>(null)

const statusMap: Record<string, string> = {
  draft: '草稿', wait_teacher_audit: '待导师审核', wait_college_review: '待院级评审',
  wait_college_audit: '待院级终审', wait_school_review: '待校级评审', wait_school_audit: '待校级终审',
  approved: '已立项', rejected: '已驳回', running: '运行中', mid_checking: '中期检查',
  conclude_apply: '待结题', concluded: '已结题'
}

function getStatusText(s: string) { return statusMap[s] || s }
function getStepIndex() {
  const s = project.value?.status
  const map: Record<string, number> = { draft: 0, wait_teacher_audit: 1, wait_college_review: 2, wait_college_audit: 3, wait_school_review: 4, wait_school_audit: 5, approved: 6 }
  return map[s] ?? (s === 'rejected' ? 0 : 7)
}

async function loadData() {
  const id = Number(route.params.id)
  try {
    const res: any = await getProject(id)
    project.value = res.data
  } catch {}
}

async function handleSubmit() {
  await submitProject(project.value.projectId)
  ElMessage.success('已提交审核')
  loadData()
}

async function handleAudit(result: string) {
  await teacherAudit({ projectId: project.value.projectId, result })
  ElMessage.success(result === 'pass' ? '审核通过' : '已驳回')
  loadData()
}

async function handleCollegeAudit(result: string) {
  await collegeAudit({ projectId: project.value.projectId, result })
  ElMessage.success(result === 'pass' ? '院级审核通过' : '已驳回')
  loadData()
}

async function handleSchoolAudit(result: string) {
  await schoolAudit({ projectId: project.value.projectId, result })
  ElMessage.success(result === 'pass' ? '校级审核通过' : '已驳回')
  loadData()
}

onMounted(loadData)
</script>
