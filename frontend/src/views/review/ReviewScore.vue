<template>
  <div class="page-container">
    <div class="page-header">
      <h2>评审打分</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-row :gutter="16">
      <el-col :span="10">
        <el-card shadow="never" class="project-info-card">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-weight:600">项目信息</span>
              <el-tag v-if="project" :type="getStatusType(project.status)" size="small">{{ getStatusText(project.status) }}</el-tag>
            </div>
          </template>
          <div v-if="projectLoading" style="text-align:center;padding:20px">
            <el-icon class="is-loading" :size="24"><Loading /></el-icon>
          </div>
          <div v-else-if="project" class="project-detail">
            <h3 class="project-title">{{ project.projectName }}</h3>
            <el-descriptions :column="1" border size="small">
              <el-descriptions-item label="项目类别">{{ project.catName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="所属学院">{{ project.collegeName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="负责人">{{ project.leaderName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="指导老师">{{ project.teacherName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="申请经费">{{ project.totalBudget?.toLocaleString() || '-' }} 元</el-descriptions-item>
              <el-descriptions-item label="项目周期">{{ project.startTime || '-' }} 至 {{ project.endTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="申报年份">{{ project.applyYear || '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
          <el-empty v-else description="项目信息加载失败" :image-size="60" />
        </el-card>
      </el-col>

      <el-col :span="14">
        <div class="card">
          <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" size="large">
            <el-form-item label="项目ID">
              <el-input :model-value="projectId" disabled />
            </el-form-item>
            <el-form-item label="评审阶段" prop="stage">
              <el-radio-group v-model="form.stage">
                <el-radio value="college">院级评审</el-radio>
                <el-radio value="school">校级评审</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-divider>打分项（每项0-25分）</el-divider>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="创新性" prop="scoreInnovation">
                  <el-slider v-model="form.scoreInnovation" :max="25" show-input />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="可行性" prop="scoreFeasibility">
                  <el-slider v-model="form.scoreFeasibility" :max="25" show-input />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="团队情况" prop="scoreTeam">
                  <el-slider v-model="form.scoreTeam" :max="25" show-input />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="应用价值" prop="scoreValue">
                  <el-slider v-model="form.scoreValue" :max="25" show-input />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="总分">
              <span style="font-size: 24px; font-weight: 700; color: var(--primary)">{{ form.scoreInnovation + form.scoreFeasibility + form.scoreTeam + form.scoreValue }}</span>
            </el-form-item>

            <el-form-item label="评审意见">
              <el-input v-model="form.opinion" type="textarea" :rows="4" placeholder="请输入评审意见" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="handleSubmit">提交评分</el-button>
              <el-button @click="$router.back()">取消</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { submitScore } from '@/api/review'
import { getProject } from '@/api/project'
import { useUserStore } from '@/stores/user'
import { Loading } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const projectLoading = ref(false)
const project = ref<any>(null)
const projectId = Number(route.params.id)

const statusMap: Record<string, string> = {
  draft: '草稿', wait_teacher_audit: '待导师审核', wait_college_review: '待院级评审',
  wait_college_audit: '待院级终审', wait_school_review: '待校级评审', wait_school_audit: '待校级终审',
  approved: '已立项', rejected: '已驳回', running: '运行中', mid_checking: '中期检查',
  conclude_apply: '待结题', concluded: '已结题'
}

function getStatusText(s: string) { return statusMap[s] || s }
function getStatusType(s: string) {
  const map: Record<string, string> = {
    draft: 'info', wait_teacher_audit: 'warning', wait_college_review: 'warning',
    wait_college_audit: 'warning', wait_school_review: 'warning', wait_school_audit: 'warning',
    approved: 'success', rejected: 'danger', running: 'success', concluded: 'success'
  }
  return map[s] || 'info'
}

async function loadProject() {
  projectLoading.value = true
  try {
    const res: any = await getProject(projectId)
    project.value = res.data
  } catch {} finally {
    projectLoading.value = false
  }
}

const form = reactive({
  stage: 'college',
  scoreInnovation: 0,
  scoreFeasibility: 0,
  scoreTeam: 0,
  scoreValue: 0,
  opinion: ''
})

const rules = {
  stage: [{ required: true, message: '请选择评审阶段', trigger: 'change' }]
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await submitScore({
      projectId,
      stage: form.stage,
      scoreInnovation: form.scoreInnovation,
      scoreFeasibility: form.scoreFeasibility,
      scoreTeam: form.scoreTeam,
      scoreValue: form.scoreValue,
      totalScore: form.scoreInnovation + form.scoreFeasibility + form.scoreTeam + form.scoreValue,
      opinion: form.opinion
    })
    ElMessage.success('评分提交成功')
    router.push(userStore.role !== 'student' ? '/reviews/my-tasks' : '/reviews')
  } catch {} finally { submitting.value = false }
}

onMounted(loadProject)
</script>

<style lang="scss" scoped>
.project-info-card {
  .project-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 12px;
    line-height: 1.4;
  }

  .project-detail {
    max-height: 600px;
    overflow-y: auto;
  }
}
</style>
