<template>
  <div class="page-container">
    <div class="page-header">
      <h2>专家管理</h2>
      <el-button type="primary" @click="showDialog = true">
        <el-icon><Plus /></el-icon> 新增专家
      </el-button>
    </div>

    <div class="card">
      <div class="filter-bar">
        <el-input v-model="filters.realName" placeholder="搜索姓名" clearable style="width: 160px" @keyup.enter="loadData" />
        <el-input v-model="filters.researchField" placeholder="研究方向" clearable style="width: 160px" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="expertId" label="ID" width="70" sortable />
        <el-table-column prop="realName" label="姓名" width="100" sortable />
        <el-table-column prop="unit" label="工作单位" min-width="160" />
        <el-table-column prop="title" label="职称" width="100" />
        <el-table-column prop="researchField" label="研究方向" min-width="140" :filters="fieldFilters" :filter-method="filterField" />
        <el-table-column prop="isInner" label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isInner ? 'primary' : 'warning'" size="small">{{ row.isInner ? '校内' : '校外' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '可用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <el-dialog v-model="showDialog" :title="editingId ? '编辑专家' : '新增专家'" width="500px">
      <el-form :model="expertForm" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="expertForm.realName" /></el-form-item>
        <el-form-item label="工作单位"><el-input v-model="expertForm.unit" /></el-form-item>
        <el-form-item label="职称"><el-input v-model="expertForm.title" /></el-form-item>
        <el-form-item label="研究方向"><el-input v-model="expertForm.researchField" /></el-form-item>
        <el-form-item label="专家类型">
          <el-switch v-model="expertForm.isInner" :active-value="1" :inactive-value="0" active-text="校内" inactive-text="校外" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="expertForm.status" :active-value="1" :inactive-value="0" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getExperts, createExpert, updateExpert, deleteExpert } from '@/api/expert'

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1), size = ref(10), total = ref(0)
const showDialog = ref(false)
const editingId = ref<number | null>(null)
const filters = reactive({ realName: '', researchField: '' })
const expertForm = reactive({ realName: '', unit: '', title: '', researchField: '', isInner: 1, status: 1 })

const fieldFilters = computed(() => {
  const fields = [...new Set(tableData.value.map(r => r.researchField).filter(Boolean))]
  return fields.map(f => ({ text: f, value: f }))
})
function filterField(value: string, row: any) { return row.researchField === value }

async function loadData() {
  loading.value = true
  try {
    const res: any = await getExperts({ page: page.value, size: size.value, ...filters })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {} finally { loading.value = false }
}

function handleEdit(row: any) {
  editingId.value = row.expertId
  Object.assign(expertForm, row)
  showDialog.value = true
}

async function handleSave() {
  if (editingId.value) {
    await updateExpert(editingId.value, expertForm)
  } else {
    await createExpert(expertForm)
  }
  ElMessage.success('保存成功')
  showDialog.value = false
  editingId.value = null
  loadData()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteExpert(row.expertId)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
