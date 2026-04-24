<template>
  <div class="page-container">
    <div class="page-header">
      <h2>里程碑管理</h2>
      <el-button type="primary" @click="showDialog = true">
        <el-icon><Plus /></el-icon> 新增里程碑
      </el-button>
    </div>

    <div class="card">
      <div class="filter-bar">
        <el-input v-model="filters.projectId" placeholder="项目ID" clearable style="width: 120px" @keyup.enter="loadData" />
        <el-select v-model="filters.status" placeholder="状态" clearable style="width: 120px" @change="loadData">
          <el-option label="待办" value="pending" />
          <el-option label="进行中" value="doing" />
          <el-option label="已完成" value="finished" />
          <el-option label="已逾期" value="overdue" />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="milestoneId" label="ID" width="70" />
        <el-table-column prop="projectId" label="项目ID" width="80" sortable />
        <el-table-column prop="milestoneName" label="里程碑名称" min-width="180" />
        <el-table-column prop="planTime" label="计划时间" width="120" sortable />
        <el-table-column prop="actualTime" label="实际完成" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="plain">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isWarning" label="预警" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isWarning" type="danger" effect="dark" size="small">预警</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status !== 'finished'" link type="success" size="small" @click="handleComplete(row)">完成</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <el-dialog v-model="showDialog" :title="editingId ? '编辑里程碑' : '新增里程碑'" width="500px">
      <el-form :model="milestoneForm" label-width="100px">
        <el-form-item label="项目ID">
          <el-input v-model="milestoneForm.projectId" />
        </el-form-item>
        <el-form-item label="里程碑名称">
          <el-input v-model="milestoneForm.milestoneName" />
        </el-form-item>
        <el-form-item label="计划时间">
          <el-date-picker v-model="milestoneForm.planTime" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="milestoneForm.status" style="width: 100%">
            <el-option label="待办" value="pending" />
            <el-option label="进行中" value="doing" />
            <el-option label="已完成" value="finished" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMilestones, createMilestone, updateMilestone, deleteMilestone } from '@/api/milestone'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1), size = ref(10), total = ref(0)
const showDialog = ref(false)
const editingId = ref<number | null>(null)
const filters = reactive({ projectId: '', status: '' })
const milestoneForm = reactive({ projectId: '', milestoneName: '', planTime: '', status: 'pending' })

function statusType(s: string) { return { pending: 'info', doing: 'warning', finished: 'success', overdue: 'danger' }[s] || 'info' }
function statusText(s: string) { return { pending: '待办', doing: '进行中', finished: '已完成', overdue: '已逾期' }[s] || s }

async function loadData() {
  loading.value = true
  try {
    const res: any = await getMilestones({ page: page.value, size: size.value, ...filters })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {} finally { loading.value = false }
}

function handleEdit(row: any) {
  editingId.value = row.milestoneId
  Object.assign(milestoneForm, { projectId: String(row.projectId), milestoneName: row.milestoneName, planTime: row.planTime, status: row.status })
  showDialog.value = true
}

async function handleComplete(row: any) {
  await updateMilestone(row.milestoneId, { status: 'finished', actualTime: dayjs().format('YYYY-MM-DD') })
  ElMessage.success('已标记完成')
  loadData()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteMilestone(row.milestoneId)
  ElMessage.success('删除成功')
  loadData()
}

async function handleSave() {
  if (editingId.value) {
    await updateMilestone(editingId.value, { ...milestoneForm, projectId: Number(milestoneForm.projectId) })
  } else {
    await createMilestone({ ...milestoneForm, projectId: Number(milestoneForm.projectId) })
  }
  ElMessage.success('保存成功')
  showDialog.value = false
  editingId.value = null
  loadData()
}

onMounted(loadData)
</script>
