<template>
  <div class="register-page">
    <div class="register-bg">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-grid"></div>
    </div>

    <div class="register-container">
      <div class="register-left">
        <div class="brand-section">
          <div class="brand-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="40" height="40">
              <path d="M12 2L2 7l10 5 10-5-10-5z"/>
              <path d="M2 17l10 5 10-5"/>
              <path d="M2 12l10 5 10-5"/>
            </svg>
          </div>
          <h1>高校创新项目管理系统</h1>
          <p>注册账号，开启您的创新之旅</p>
        </div>
      </div>

      <div class="register-right">
        <div class="register-card">
          <div class="card-header">
            <h2>创建账号</h2>
            <p>填写以下信息完成注册</p>
          </div>

          <el-form ref="formRef" :model="form" :rules="rules" size="large" label-position="top">
            <div class="form-row">
              <el-form-item label="学号/工号" prop="username">
                <el-input v-model="form.username" placeholder="请输入学号或工号" clearable />
              </el-form-item>
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="form.realName" placeholder="请输入真实姓名" clearable />
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="密码" prop="password">
                <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
              </el-form-item>
            </div>

            <el-form-item label="角色" prop="role">
              <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
                <el-option label="学生" value="student" />
                <el-option label="指导老师" value="teacher" />
              </el-select>
            </el-form-item>

            <div class="form-row">
              <el-form-item label="所属学院" prop="collegeId">
                <el-select v-model="form.collegeId" placeholder="请选择学院" style="width: 100%">
                  <el-option v-for="c in colleges" :key="c.collegeId" :label="c.collegeName" :value="c.collegeId" />
                </el-select>
              </el-form-item>
              <el-form-item v-if="form.role === 'student'" label="专业">
                <el-input v-model="form.major" placeholder="请输入专业" />
              </el-form-item>
            </div>

            <div class="form-row">
              <el-form-item label="手机号">
                <el-input v-model="form.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="form.email" placeholder="请输入邮箱" />
              </el-form-item>
            </div>

            <el-form-item>
              <el-button type="primary" :loading="loading" class="register-btn" @click="handleRegister">
                {{ loading ? '注册中...' : '注 册' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="card-footer">
            <span>已有账号？</span>
            <router-link to="/login" class="link">返回登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { register } from '@/api/auth'
import { getColleges } from '@/api/college'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const colleges = ref<any[]>([])

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  role: '',
  collegeId: undefined as number | undefined,
  major: '',
  phone: '',
  email: ''
})

const validateConfirm = (_rule: any, value: string, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入学号/工号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  collegeId: [{ required: true, message: '请选择学院', trigger: 'change' }]
}

onMounted(async () => {
  try {
    const res: any = await getColleges()
    colleges.value = res.data || []
  } catch {}
})

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await register({
      username: form.username,
      password: form.password,
      realName: form.realName,
      role: form.role,
      collegeId: form.collegeId,
      major: form.major,
      phone: form.phone,
      email: form.email
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {} finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  width: 100vw;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f0c29 0%, #1a1a2e 40%, #16213e 100%);
  position: relative;
  overflow-y: auto;
  padding: 40px 0;
}

.register-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;

  .bg-shape {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.25;
  }
  .shape-1 { width: 500px; height: 500px; background: #7c3aed; top: -15%; right: -5%; animation: float 15s ease-in-out infinite; }
  .shape-2 { width: 400px; height: 400px; background: #06d6a0; bottom: -10%; left: -5%; animation: float 18s ease-in-out infinite reverse; }
  .bg-grid {
    position: absolute; inset: 0;
    background-image: linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
    background-size: 60px 60px;
  }
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

.register-container {
  display: flex;
  width: 880px;
  min-height: 600px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0,0,0,0.4);
  position: relative;
  z-index: 1;
}

.register-left {
  width: 280px;
  padding: 48px 32px;
  background: rgba(26, 26, 46, 0.85);
  display: flex;
  flex-direction: column;
  justify-content: center;

  .brand-section {
    .brand-icon {
      width: 64px; height: 64px;
      background: linear-gradient(135deg, #4361ee, #7c3aed);
      border-radius: 16px;
      display: flex; align-items: center; justify-content: center;
      color: #fff; margin-bottom: 20px;
    }
    h1 { font-size: 22px; font-weight: 700; color: #fff; margin-bottom: 8px; }
    p { font-size: 14px; color: rgba(255,255,255,0.5); line-height: 1.7; }
  }
}

.register-right {
  flex: 1;
  padding: 40px 36px;
  background: #fff;
  overflow-y: auto;

  .card-header {
    margin-bottom: 24px;
    h2 { font-size: 24px; font-weight: 700; color: #1a1a2e; margin-bottom: 6px; }
    p { font-size: 14px; color: #9ca3af; }
  }

  .form-row {
    display: flex;
    gap: 16px;
    & > * { flex: 1; }
  }

  :deep(.el-input__wrapper) {
    border-radius: 10px;
    box-shadow: 0 0 0 1px #e5e7eb;
    &:hover, &.is-focus { box-shadow: 0 0 0 2px #4361ee; }
  }

  .register-btn {
    width: 100%; height: 46px; border-radius: 10px;
    font-size: 16px; font-weight: 600; letter-spacing: 4px;
    background: linear-gradient(135deg, #4361ee 0%, #7c3aed 100%);
    border: none; transition: all 0.3s;
    &:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(67,97,238,0.4); }
  }

  .card-footer {
    text-align: center; margin-top: 16px; font-size: 14px; color: #9ca3af;
    .link { color: #4361ee; text-decoration: none; font-weight: 500; margin-left: 4px; &:hover { color: #7c3aed; } }
  }
}
</style>
