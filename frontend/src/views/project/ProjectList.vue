<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isStudent ? '我的项目' : '项目管理' }}</h2>
      <el-button v-if="!isStudent || true" type="primary" @click="$router.push('/projects/create')">
        <el-icon><Plus /></el-icon> 创建项目
      </el-button>
    </div>

    <div class="card">
      <div class="filter-bar">
        <el-input v-model="filters.projectName" placeholder="搜索项目名称" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="filters.status" placeholder="项目状态" clearable style="width: 160px" @change="loadData">
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
        <el-select v-if="!isStudent" v-model="filters.collegeId" placeholder="所属学院" clearable style="width: 160px" @change="loadData">
          <el-option v-for="c in colleges" :key="c.collegeId" :label="c.collegeName" :value="c.collegeId" />
        </el-select>
        <el-select v-model="filters.applyYear" placeholder="申报年份" clearable style="width: 120px" @change="loadData">
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading" @sort-change="handleSort">
        <el-table-column prop="projectId" label="ID" width="70" sortable />
        <el-table-column prop="projectName" label="项目名称" min-width="200" sortable show-overflow-tooltip>
          <template #default="{ row }">
            <router-link :to="`/projects/${row.projectId}`" class="project-link">{{ row.projectName }}</router-link>
          </template>
        </el-table-column>
        <el-table-column prop="leaderName" label="负责人" width="100" sortable />
        <el-table-column prop="teacherName" label="指导老师" width="100" sortable />
        <el-table-column v-if="!isStudent" prop="collegeName" label="所属学院" width="120" sortable :filters="collegeFilters" :filter-method="filterCollege" />
        <el-table-column prop="catName" label="类别" width="110" :filters="catFilters" :filter-method="filterCat" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <span :class="['status-tag', row.status]">{{ getStatusText(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalBudget" label="经费(元)" width="100" sortable>
          <template #default="{ row }">{{ row.totalBudget?.toLocaleString() || '-' }}</template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申报时间" width="160" sortable />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/projects/${row.projectId}`)">查看</el-button>
            <el-button v-if="canDelete" link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getProjects, deleteProject } from '@/api/project'
import { getColleges } from '@/api/college'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isStudent = computed(() => userStore.role === 'student')
const canDelete = computed(() => ['college_admin', 'school_admin'].includes(userStore.role))

const loading = ref(false)
const tableData = ref<any[]>([])
const colleges = ref<any[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const filters = reactive({
  projectName: '',
  status: '',
  collegeId: undefined as number | undefined,
  applyYear: undefined as number | undefined
})

const statusOptions = [
  { value: 'draft', label: '草稿' },
  { value: 'wait_teacher_audit', label: '待导师审核' },
  { value: 'wait_college_assign', label: '待院级分配' },
  { value: 'wait_college_review', label: '待院级评审' },
  { value: 'wait_college_audit', label: '待院级终审' },
  { value: 'wait_school_assign', label: '待校级分配' },
  { value: 'wait_school_review', label: '待校级评审' },
  { value: 'wait_school_audit', label: '待校级终审' },
  { value: 'approved', label: '已立项' },
  { value: 'rejected', label: '已驳回' },
  { value: 'running', label: '运行中' },
  { value: 'mid_checking', label: '中期检查' },
  { value: 'conclude_apply', label: '待结题' },
  { value: 'concluded', label: '已结题' }
]

const yearOptions = [2024, 2025, 2026]
const collegeFilters = computed(() => colleges.value.map(c => ({ text: c.collegeName, value: c.collegeName })))
const catFilters = [
  { text: '创新训练', value: '创新训练' },
  { text: '创业训练', value: '创业训练' },
  { text: '创业实践', value: '创业实践' }
]

function getStatusText(status: string) {
  return statusOptions.find(s => s.value === status)?.label || status
}

function filterCollege(value: string, row: any) { return row.collegeName === value }
function filterCat(value: string, row: any) { return row.catName === value }
function handleSort() { loadData() }

async function loadData() {
  loading.value = true
  try {
    const params: any = { page: page.value, size: size.value, ...filters }
    // 清理空值
    Object.keys(params).forEach(k => { if (params[k] === '' || params[k] === undefined) delete params[k] })
    const res: any = await getProjects(params)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {} finally { loading.value = false }
}

function resetFilters() {
  Object.assign(filters, { projectName: '', status: '', collegeId: undefined, applyYear: undefined })
  page.value = 1
  loadData()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除项目「${row.projectName}」？`, '提示', { type: 'warning' })
  await deleteProject(row.projectId)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(async () => {
  if (!isStudent.value) {
    try { const res: any = await getColleges(); colleges.value = res.data || [] } catch {}
  }
  loadData()
})
</script>

<style lang="scss" scoped>
.project-link { color: var(--primary); text-decoration: none; font-weight: 500; &:hover { text-decoration: underline; } }
</style>
