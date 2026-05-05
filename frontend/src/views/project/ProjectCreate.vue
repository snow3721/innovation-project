<template>
  <div class="page-container">
    <div class="page-header">
      <h2>创建项目</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div class="card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" size="large">
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="form.projectName" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目类别" prop="catId">
              <el-select v-model="form.catId" placeholder="请选择类别" style="width: 100%">
                <el-option v-for="c in categories" :key="c.catId" :label="c.catName" :value="c.catId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属学院" prop="collegeId">
              <el-select v-model="form.collegeId" placeholder="请选择学院" style="width: 100%" @change="onCollegeChange">
                <el-option v-for="c in colleges" :key="c.collegeId" :label="c.collegeName" :value="c.collegeId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请经费" prop="totalBudget">
              <el-input-number v-model="form.totalBudget" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="指导老师" prop="teacherId">
              <el-select
                v-model="form.teacherId"
                placeholder="请选择指导老师"
                filterable
                style="width: 100%"
                :loading="teachersLoading"
              >
                <el-option
                  v-for="t in teachers"
                  :key="t.userId"
                  :label="t.realName + (t.major ? '（' + t.major + '）' : '')"
                  :value="t.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker v-model="form.startTime" type="date" value-format="YYYY-MM-DD" placeholder="选择开始时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker v-model="form.endTime" type="date" value-format="YYYY-MM-DD" placeholder="选择结束时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">项目内容</el-divider>
        <el-form-item label="项目简介">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入项目简介" />
        </el-form-item>
        <el-form-item label="创新点">
          <el-input v-model="form.innovationPointsStr" type="textarea" :rows="2" placeholder="每行一个创新点" />
        </el-form-item>
        <el-form-item label="技术路线">
          <el-input v-model="form.techRoute" type="textarea" :rows="3" placeholder="请描述技术路线" />
        </el-form-item>

        <el-divider content-position="left">团队成员</el-divider>
        <el-form-item label="团队成员">
          <div class="member-list">
            <div v-for="(m, idx) in form.members" :key="idx" class="member-row">
              <el-input v-model="m.userId" placeholder="学号" style="width: 150px" />
              <el-select v-model="m.role" style="width: 120px">
                <el-option label="负责人" value="leader" />
                <el-option label="成员" value="normal" />
              </el-select>
              <el-button type="danger" link @click="form.members.splice(idx, 1)">移除</el-button>
            </div>
            <el-button type="primary" plain @click="form.members.push({ userId: '', role: 'normal' })">添加成员</el-button>
          </div>
        </el-form-item>

        <el-divider content-position="left">附件上传</el-divider>
        <el-form-item label="申报材料">
          <FileUpload file-type="apply" :limit="5" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit('draft')">保存草稿</el-button>
          <el-button type="success" :loading="submitting" @click="handleSubmit('submit')">保存并提交</el-button>
          <el-button @click="$router.back()">取消</el-button>
          <span v-if="autoSaveTip" style="margin-left: 16px; color: #67c23a; font-size: 13px">{{ autoSaveTip }}</span>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { createProject, updateProject, submitProject } from '@/api/project'
import { getColleges } from '@/api/college'
import { getCategories } from '@/api/category'
import { getTeachers } from '@/api/user'
import FileUpload from '@/components/FileUpload.vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const colleges = ref<any[]>([])
const categories = ref<any[]>([])
const teachers = ref<any[]>([])
const teachersLoading = ref(false)
const projectId = ref<number | null>(null)
const autoSaveTip = ref('')
let autoSaveTimer: ReturnType<typeof setInterval> | null = null

const form = reactive({
  projectName: '',
  catId: undefined as number | undefined,
  collegeId: undefined as number | undefined,
  teacherId: undefined as number | undefined,
  totalBudget: 0,
  startTime: '',
  endTime: '',
  content: '',
  innovationPointsStr: '',
  techRoute: '',
  members: [] as { userId: string; role: string }[]
})

const rules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  catId: [{ required: true, message: '请选择类别', trigger: 'change' }],
  collegeId: [{ required: true, message: '请选择学院', trigger: 'change' }],
  teacherId: [{ required: true, message: '请选择指导老师', trigger: 'change' }]
}

async function loadTeachers(collegeId?: number) {
  teachersLoading.value = true
  try {
    const res: any = await getTeachers({ collegeId })
    teachers.value = res.data || []
  } catch {
    teachers.value = []
  } finally {
    teachersLoading.value = false
  }
}

function onCollegeChange(collegeId: number) {
  form.teacherId = undefined
  loadTeachers(collegeId)
}

async function handleSubmit(action: string) {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const data = {
      ...form,
      innovationPoints: form.innovationPointsStr.split('\n').filter(s => s.trim()),
      members: form.members.filter(m => m.userId)
    }
    const res: any = await createProject(data)
    if (res.data?.projectId) {
      projectId.value = res.data.projectId
      startAutoSave()
    }
    if (action === 'submit' && res.data?.projectId) {
      await submitProject(res.data.projectId)
    }
    ElMessage.success(action === 'draft' ? '草稿已保存' : '项目已提交')
    router.push('/projects')
  } catch {} finally { submitting.value = false }
}

// 草稿自动保存
function startAutoSave() {
  if (autoSaveTimer) clearInterval(autoSaveTimer)
  autoSaveTimer = setInterval(async () => {
    if (!projectId.value) return
    try {
      await updateProject(projectId.value, {
        projectName: form.projectName,
        catId: form.catId,
        collegeId: form.collegeId,
        teacherId: form.teacherId,
        totalBudget: form.totalBudget,
        startTime: form.startTime,
        endTime: form.endTime,
        content: form.content,
        innovationPointsStr: form.innovationPointsStr,
        techRoute: form.techRoute,
      })
      autoSaveTip.value = '已自动保存 ' + new Date().toLocaleTimeString()
    } catch {
      autoSaveTip.value = '自动保存失败'
    }
  }, 60000) // 每60秒自动保存
}

onMounted(async () => {
  try {
    const [cRes, catRes]: any[] = await Promise.all([getColleges(), getCategories()])
    colleges.value = cRes.data || []
    categories.value = catRes.data || []
  } catch {}
})

onUnmounted(() => {
  if (autoSaveTimer) {
    clearInterval(autoSaveTimer)
    autoSaveTimer = null
  }
})
</script>

<style lang="scss" scoped>
.member-list { width: 100%; }
.member-row { display: flex; gap: 10px; align-items: center; margin-bottom: 8px; }
</style>
