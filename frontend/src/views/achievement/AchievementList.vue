<template>
  <div class="page-container">
    <div class="page-header">
      <h2>成果管理</h2>
      <el-button type="primary" @click="$router.push('/achievements/create')">
        <el-icon><Plus /></el-icon> 提交成果
      </el-button>
    </div>

    <div class="card">
      <div class="filter-bar">
        <el-input v-model="filters.projectId" placeholder="项目ID" clearable style="width: 120px" @keyup.enter="loadData" />
        <el-select v-model="filters.type" placeholder="成果类型" clearable style="width: 140px" @change="loadData">
          <el-option label="专利" value="patent" />
          <el-option label="论文" value="paper" />
          <el-option label="软件著作权" value="software" />
          <el-option label="竞赛获奖" value="competition" />
          <el-option label="商业落地" value="business" />
          <el-option label="其他" value="other" />
        </el-select>
        <el-select v-model="filters.status" placeholder="状态" clearable style="width: 120px" @change="loadData">
          <el-option label="申请中" value="applying" />
          <el-option label="已批准" value="approved" />
          <el-option label="已发表" value="published" />
          <el-option label="已落地" value="landed" />
        </el-select>
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="achievementId" label="ID" width="70" />
        <el-table-column prop="projectId" label="项目ID" width="80" sortable />
        <el-table-column prop="name" label="成果名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="110" :filters="typeFilters" :filter-method="filterType">
          <template #default="{ row }">{{ typeMap[row.type] || row.type }}</template>
        </el-table-column>
        <el-table-column prop="achievementNo" label="编号" width="140" />
        <el-table-column prop="publishTime" label="发布时间" width="120" sortable />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="plain" size="small">{{ statusMap[row.status] || row.status }}</el-tag>
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

    <el-dialog v-model="showDialog" title="编辑成果" width="600px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="成果名称"><el-input v-model="editForm.name" /></el-form-item>
        <el-form-item label="成果类型">
          <el-select v-model="editForm.type" style="width: 100%">
            <el-option v-for="(v, k) in typeMap" :key="k" :label="v" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="成果编号"><el-input v-model="editForm.achievementNo" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option v-for="(v, k) in statusMap" :key="k" :label="v" :value="k" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAchievements, updateAchievement, deleteAchievement } from '@/api/achievement'

const loading = ref(false)
const tableData = ref<any[]>([])
const page = ref(1), size = ref(10), total = ref(0)
const showDialog = ref(false)
const editForm = reactive({ achievementId: 0, name: '', type: '', achievementNo: '', status: '' })
const filters = reactive({ projectId: '', type: '', status: '' })

const typeMap: Record<string, string> = { patent: '专利', paper: '论文', software: '软件著作权', competition: '竞赛获奖', business: '商业落地', other: '其他' }
const statusMap: Record<string, string> = { applying: '申请中', approved: '已批准', published: '已发表', landed: '已落地' }
const typeFilters = Object.entries(typeMap).map(([k, v]) => ({ text: v, value: k }))
function filterType(value: string, row: any) { return row.type === value }
function statusType(s: string): '' | 'success' | 'warning' | 'danger' | 'info' | 'primary' { return { applying: 'warning', approved: 'success', published: 'primary', landed: 'success' }[s] as any || 'info' }

async function loadData() {
  loading.value = true
  try {
    const res: any = await getAchievements({ page: page.value, size: size.value, ...filters })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {} finally { loading.value = false }
}

function handleEdit(row: any) {
  Object.assign(editForm, row)
  showDialog.value = true
}

async function handleSaveEdit() {
  await updateAchievement(editForm.achievementId, editForm)
  ElMessage.success('保存成功')
  showDialog.value = false
  loadData()
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteAchievement(row.achievementId)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
