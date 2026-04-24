<template>
  <div class="page-container">
    <div class="page-header">
      <h2>学院管理</h2>
      <el-button type="primary" @click="showDialog = true">
        <el-icon><Plus /></el-icon> 新增学院
      </el-button>
    </div>

    <div class="card">
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="collegeId" label="ID" width="70" sortable />
        <el-table-column prop="collegeName" label="学院名称" min-width="200" />
        <el-table-column prop="sort" label="排序" width="100" sortable />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="showDialog" :title="editingId ? '编辑学院' : '新增学院'" width="400px">
      <el-form :model="collegeForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="collegeForm.collegeName" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="collegeForm.sort" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="collegeForm.status" :active-value="1" :inactive-value="0" /></el-form-item>
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
import { getColleges, createCollege, updateCollege, deleteCollege } from '@/api/college'

const loading = ref(false)
const tableData = ref<any[]>([])
const showDialog = ref(false)
const editingId = ref<number | null>(null)
const collegeForm = reactive({ collegeName: '', sort: 0, status: 1 })

async function loadData() {
  loading.value = true
  try {
    const res: any = await getColleges()
    tableData.value = res.data || []
  } catch {} finally { loading.value = false }
}

function handleEdit(row: any) {
  editingId.value = row.collegeId
  Object.assign(collegeForm, row)
  showDialog.value = true
}

async function handleSave() {
  if (editingId.value) {
    await updateCollege(editingId.value, collegeForm)
  } else {
    await createCollege(collegeForm)
  }
  ElMessage.success('保存成功')
  showDialog.value = false
  editingId.value = null
  loadData()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteCollege(row.collegeId)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
