<template>
  <div class="page-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="showDialog = true">
        <el-icon><Plus /></el-icon> 新增用户
      </el-button>
    </div>

    <div class="card">
      <div class="filter-bar">
        <el-input v-model="filters.realName" placeholder="搜索姓名" clearable style="width: 160px" @keyup.enter="loadData" />
        <el-select v-model="filters.role" placeholder="角色" clearable style="width: 140px" @change="loadData">
          <el-option label="学生" value="student" />
          <el-option label="导师" value="teacher" />
          <el-option label="院管理员" value="college_admin" />
          <el-option label="校管理员" value="school_admin" />
          <el-option label="专家" value="expert" />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="userId" label="ID" width="70" sortable />
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" sortable />
        <el-table-column prop="role" label="角色" width="100" :filters="roleFilters" :filter-method="filterRole">
          <template #default="{ row }">{{ roleMap[row.role] || row.role }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="collegeName" label="学院" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" sortable />
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

    <el-dialog v-model="showDialog" :title="editingId ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="账号"><el-input v-model="userForm.username" :disabled="!!editingId" /></el-form-item>
        <el-form-item v-if="!editingId" label="密码"><el-input v-model="userForm.password" type="password" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="userForm.realName" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.role" style="width: 100%">
            <el-option v-for="(v, k) in roleMap" :key="k" :label="v" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="userForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="userForm.email" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="userForm.status" :active-value="1" :inactive-value="0" />
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
import { getUsers, createUser, updateUser, deleteUser } from '@/api/user'

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1), size = ref(10), total = ref(0)
const showDialog = ref(false)
const editingId = ref<number | null>(null)
const filters = reactive({ realName: '', role: '' })
const userForm = reactive({ username: '', password: '', realName: '', role: 'student', phone: '', email: '', status: 1 })

const roleMap: Record<string, string> = { student: '学生', teacher: '导师', college_admin: '院管理员', school_admin: '校管理员', expert: '专家' }
const roleFilters = Object.entries(roleMap).map(([k, v]) => ({ text: v, value: k }))
function filterRole(value: string, row: any) { return row.role === value }

async function loadData() {
  loading.value = true
  try {
    const res: any = await getUsers({ page: page.value, size: size.value, ...filters })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {} finally { loading.value = false }
}

function handleEdit(row: any) {
  editingId.value = row.userId
  Object.assign(userForm, { username: row.username, password: '', realName: row.realName, role: row.role, phone: row.phone || '', email: row.email || '', status: row.status })
  showDialog.value = true
}

async function handleSave() {
  if (editingId.value) {
    await updateUser(editingId.value, userForm)
  } else {
    await createUser(userForm)
  }
  ElMessage.success('保存成功')
  showDialog.value = false
  editingId.value = null
  loadData()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteUser(row.userId)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
