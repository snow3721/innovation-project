<template>
  <div class="page-container">
    <div class="page-header">
      <h2>评审管理</h2>
    </div>

    <div class="card">
      <div class="filter-bar">
        <el-select v-model="filters.stage" placeholder="评审阶段" clearable style="width: 160px" @change="loadScores">
          <el-option label="院级评审" value="college" />
          <el-option label="校级评审" value="school" />
        </el-select>
        <el-input v-model="filters.projectId" placeholder="项目ID" clearable style="width: 120px" @clear="loadScores" @keyup.enter="loadScores" />
        <el-button type="primary" @click="loadScores">搜索</el-button>
      </div>

      <el-table :data="scoreData" stripe v-loading="loading">
        <el-table-column prop="scoreId" label="ID" width="70" />
        <el-table-column prop="projectId" label="项目ID" width="80" sortable />
        <el-table-column prop="expertName" label="评审专家" width="100" />
        <el-table-column prop="reviewStage" label="评审阶段" width="100">
          <template #default="{ row }">{{ row.reviewStage === 'college' ? '院级' : '校级' }}</template>
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
        <el-table-column prop="scoreTime" label="评审时间" width="160" />
      </el-table>

      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, sizes, prev, pager, next" @size-change="loadScores" @current-change="loadScores" />
      </div>
    </div>

    <el-card style="margin-top: 16px">
      <template #header><span style="font-weight:600">专家分配</span></template>
      <div class="filter-bar">
        <el-input v-model="assignForm.projectId" placeholder="项目ID" style="width: 120px" />
        <el-select v-model="assignForm.expertId" placeholder="选择专家" style="width: 200px">
          <el-option v-for="e in experts" :key="e.expertId" :label="e.realName + ' - ' + e.researchField" :value="e.expertId" />
        </el-select>
        <el-select v-model="assignForm.stage" placeholder="评审阶段" style="width: 120px">
          <el-option label="院级" value="college" />
          <el-option label="校级" value="school" />
        </el-select>
        <el-button type="primary" @click="handleAssign">分配专家</el-button>
      </div>

      <el-table :data="assignmentData" stripe>
        <el-table-column prop="assignmentId" label="ID" width="70" />
        <el-table-column prop="projectId" label="项目ID" width="80" />
        <el-table-column prop="expertId" label="专家ID" width="80" />
        <el-table-column prop="stage" label="阶段" width="80">
          <template #default="{ row }">{{ row.stage === 'college' ? '院级' : '校级' }}</template>
        </el-table-column>
        <el-table-column prop="assignTime" label="分配时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReviewScores, getAssignments, assignExpert } from '@/api/review'
import { getExperts } from '@/api/expert'

const loading = ref(false)
const scoreData = ref<any[]>([])
const assignmentData = ref<any[]>([])
const experts = ref<any[]>([])
const page = ref(1), size = ref(10), total = ref(0)

const filters = reactive({ stage: '', projectId: '' })
const assignForm = reactive({ projectId: '', expertId: undefined as number | undefined, stage: 'college' })

async function loadScores() {
  loading.value = true
  try {
    const res: any = await getReviewScores({ page: page.value, size: size.value, ...filters })
    scoreData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {} finally { loading.value = false }
}

async function loadAssignments() {
  try {
    const res: any = await getAssignments({ page: 1, size: 50 })
    assignmentData.value = res.data?.list || []
  } catch {}
}

async function handleAssign() {
  if (!assignForm.projectId || !assignForm.expertId) {
    ElMessage.warning('请填写完整信息')
    return
  }
  await assignExpert({ projectId: Number(assignForm.projectId), expertId: assignForm.expertId, stage: assignForm.stage })
  ElMessage.success('分配成功')
  loadAssignments()
}

onMounted(async () => {
  try { const res: any = await getExperts({ page: 1, size: 100 }); experts.value = res.data?.list || [] } catch {}
  loadScores()
  loadAssignments()
})
</script>
