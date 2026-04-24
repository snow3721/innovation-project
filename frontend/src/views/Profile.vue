<template>
  <div class="page-container">
    <div class="page-header"><h2>个人中心</h2></div>

    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="never">
          <div class="profile-card">
            <el-avatar :size="80" class="profile-avatar">{{ userStore.realName?.charAt(0) }}</el-avatar>
            <h3>{{ userStore.realName }}</h3>
            <p>{{ roleText }}</p>
            <el-descriptions :column="1" style="margin-top: 16px" border>
              <el-descriptions-item label="账号">{{ userStore.username }}</el-descriptions-item>
              <el-descriptions-item label="学院ID">{{ userStore.collegeId || '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">修改密码</span></template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 400px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="form.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="form.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { changePassword } from '@/api/user'

const userStore = useUserStore()
const formRef = ref<FormInstance>()

const roleText = computed(() => {
  const map: Record<string, string> = { student: '学生', teacher: '导师', college_admin: '院管理员', school_admin: '校管理员', expert: '评审专家' }
  return map[userStore.role] || ''
})

const form = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateConfirm = (_rule: any, value: string, callback: any) => {
  if (value !== form.newPassword) callback(new Error('两次密码不一致'))
  else callback()
}

const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, min: 6, message: '至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirm, trigger: 'blur' }]
}

async function handleChangePassword() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    await changePassword({ oldPassword: form.oldPassword, newPassword: form.newPassword })
    ElMessage.success('密码修改成功')
    Object.assign(form, { oldPassword: '', newPassword: '', confirmPassword: '' })
  } catch {}
}
</script>

<style lang="scss" scoped>
.profile-card {
  display: flex; flex-direction: column; align-items: center; padding: 20px 0;
  .profile-avatar {
    background: linear-gradient(135deg, #4361ee, #7c3aed);
    color: #fff; font-size: 32px; font-weight: 700; margin-bottom: 12px;
  }
  h3 { font-size: 20px; font-weight: 600; margin-bottom: 4px; }
  p { color: var(--text-secondary); font-size: 14px; }
}
</style>
