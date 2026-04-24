<template>
  <div class="page-container">
    <div class="page-header">
      <h2>评审打分</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div class="card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" size="large">
        <el-form-item label="项目ID">
          <el-input :model-value="projectId" disabled />
        </el-form-item>
        <el-form-item label="评审阶段" prop="stage">
          <el-radio-group v-model="form.stage">
            <el-radio value="college">院级评审</el-radio>
            <el-radio value="school">校级评审</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-divider>打分项（每项0-25分）</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="创新性" prop="scoreInnovation">
              <el-slider v-model="form.scoreInnovation" :max="25" show-input />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="可行性" prop="scoreFeasibility">
              <el-slider v-model="form.scoreFeasibility" :max="25" show-input />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="团队情况" prop="scoreTeam">
              <el-slider v-model="form.scoreTeam" :max="25" show-input />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应用价值" prop="scoreValue">
              <el-slider v-model="form.scoreValue" :max="25" show-input />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="总分">
          <span style="font-size: 24px; font-weight: 700; color: var(--primary)">{{ form.scoreInnovation + form.scoreFeasibility + form.scoreTeam + form.scoreValue }}</span>
        </el-form-item>

        <el-form-item label="评审意见">
          <el-input v-model="form.opinion" type="textarea" :rows="4" placeholder="请输入评审意见" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交评分</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { submitScore } from '@/api/review'

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const projectId = Number(route.params.id)

const form = reactive({
  stage: 'college',
  scoreInnovation: 0,
  scoreFeasibility: 0,
  scoreTeam: 0,
  scoreValue: 0,
  opinion: ''
})

const rules = {
  stage: [{ required: true, message: '请选择评审阶段', trigger: 'change' }]
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await submitScore({
      projectId,
      stage: form.stage,
      scoreInnovation: form.scoreInnovation,
      scoreFeasibility: form.scoreFeasibility,
      scoreTeam: form.scoreTeam,
      scoreValue: form.scoreValue,
      totalScore: form.scoreInnovation + form.scoreFeasibility + form.scoreTeam + form.scoreValue,
      opinion: form.opinion
    })
    ElMessage.success('评分提交成功')
    router.push('/reviews')
  } catch {} finally { submitting.value = false }
}
</script>
