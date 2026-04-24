<template>
  <div class="page-container">
    <div class="page-header">
      <h2>项目类别管理</h2>
      <el-button type="primary" @click="showDialog = true">
        <el-icon><Plus /></el-icon> 新增类别
      </el-button>
    </div>

    <div class="card">
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="catId" label="ID" width="70" />
        <el-table-column prop="catName" label="类别名称" min-width="200" />
        <el-table-column prop="remark" label="说明" min-width="300" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="showDialog" :title="editingId ? '编辑类别' : '新增类别'" width="400px">
      <el-form :model="catForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="catForm.catName" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="catForm.remark" type="textarea" :rows="3" /></el-form-item>
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
import { getCategories, createCategory, updateCategory, deleteCategory } from '@/api/category'

const loading = ref(false)
const tableData = ref<any[]>([])
const showDialog = ref(false)
const editingId = ref<number | null>(null)
const catForm = reactive({ catName: '', remark: '' })

async function loadData() {
  loading.value = true
  try {
    const res: any = await getCategories()
    tableData.value = res.data || []
  } catch {} finally { loading.value = false }
}

function handleEdit(row: any) {
  editingId.value = row.catId
  Object.assign(catForm, row)
  showDialog.value = true
}

async function handleSave() {
  if (editingId.value) {
    await updateCategory(editingId.value, catForm)
  } else {
    await createCategory(catForm)
  }
  ElMessage.success('保存成功')
  showDialog.value = false
  editingId.value = null
  loadData()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteCategory(row.catId)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
