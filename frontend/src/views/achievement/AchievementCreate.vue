<template>
  <div class="page-container">
    <div class="page-header">
      <h2>提交成果</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div class="card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" size="large">
        <el-form-item label="关联项目" prop="projectId">
          <el-input v-model.number="form.projectId" placeholder="请输入项目ID" />
        </el-form-item>
        <el-form-item label="成果类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="专利" value="patent" />
            <el-option label="论文" value="paper" />
            <el-option label="软件著作权" value="software" />
            <el-option label="竞赛获奖" value="competition" />
            <el-option label="商业落地" value="business" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="成果名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入成果名称" />
        </el-form-item>
        <el-form-item label="成果编号">
          <el-input v-model="form.achievementNo" placeholder="如专利号、论文DOI等" />
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="form.publishTime" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="成果状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="申请中" value="applying" />
            <el-option label="已批准" value="approved" />
            <el-option label="已发表" value="published" />
            <el-option label="已落地" value="landed" />
          </el-select>
        </el-form-item>

        <el-divider>扩展信息</el-divider>
        <el-form-item v-if="form.type === 'patent'" label="申请人">
          <el-input v-model="extendForm.applicant" placeholder="请输入申请人" />
        </el-form-item>
        <el-form-item v-if="form.type === 'paper'" label="期刊">
          <el-input v-model="extendForm.journal" placeholder="请输入期刊名" />
        </el-form-item>
        <el-form-item v-if="form.type === 'competition'" label="赛事名称">
          <el-input v-model="extendForm.contestName" placeholder="请输入赛事名称" />
        </el-form-item>

        <el-divider>证明材料</el-divider>
        <el-form-item label="上传附件">
          <FileUpload file-type="achievement" :limit="5" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交成果</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { createAchievement } from '@/api/achievement'
import FileUpload from '@/components/FileUpload.vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive({
  projectId: undefined as number | undefined,
  type: '',
  name: '',
  achievementNo: '',
  publishTime: '',
  status: 'applying'
})

const extendForm = reactive({
  applicant: '',
  journal: '',
  contestName: ''
})

const rules = {
  projectId: [{ required: true, message: '请输入项目ID', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await createAchievement({ ...form, extend: extendForm })
    ElMessage.success('成果提交成功')
    router.push('/achievements')
  } catch {} finally { submitting.value = false }
}
</script>
