<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
      <div class="bg-grid"></div>
    </div>

    <div class="login-container">
      <div class="login-left">
        <div class="brand-section">
          <div class="brand-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="48" height="48">
              <path d="M12 2L2 7l10 5 10-5-10-5z"/>
              <path d="M2 17l10 5 10-5"/>
              <path d="M2 12l10 5 10-5"/>
            </svg>
          </div>
          <h1 class="brand-title">高校创新项目管理系统</h1>
          <p class="brand-desc">覆盖项目全生命周期管理的数字化平台，<br>助力创新创业项目高效孵化</p>
        </div>

        <div class="feature-list">
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>在线申报，多级审批流程一键推进</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>智能评审，专家在线打分自动流转</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>进度追踪，里程碑预警实时掌握</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>数据驱动，多维统计分析辅助决策</span>
          </div>
        </div>
      </div>

      <div class="login-right">
        <div class="login-card">
          <div class="card-header">
            <h2>欢迎回来</h2>
            <p>请输入您的账号信息登录系统</p>
          </div>

          <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleLogin">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入学号/工号"
                prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                class="login-btn"
                @click="handleLogin"
              >
                {{ loading ? '登录中...' : '登 录' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="card-footer">
            <span>还没有账号？</span>
            <router-link to="/register" class="link">立即注册</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入学号/工号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res: any = await login(form)
    userStore.setUser(res.data)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // error handled in interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f0c29 0%, #1a1a2e 40%, #16213e 100%);
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;

  .bg-shape {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.3;
  }

  .shape-1 {
    width: 500px;
    height: 500px;
    background: #4361ee;
    top: -10%;
    right: -5%;
    animation: float 15s ease-in-out infinite;
  }

  .shape-2 {
    width: 400px;
    height: 400px;
    background: #06d6a0;
    bottom: -10%;
    left: -5%;
    animation: float 18s ease-in-out infinite reverse;
  }

  .shape-3 {
    width: 300px;
    height: 300px;
    background: #118ab2;
    top: 40%;
    left: 30%;
    animation: float 20s ease-in-out infinite;
  }

  .bg-grid {
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
    background-size: 60px 60px;
  }
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

.login-container {
  display: flex;
  width: 920px;
  min-height: 520px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0,0,0,0.4);
  position: relative;
  z-index: 1;
  backdrop-filter: blur(20px);
}

.login-left {
  flex: 1;
  padding: 48px 40px;
  background: rgba(26, 26, 46, 0.85);
  display: flex;
  flex-direction: column;
  justify-content: center;
  border-right: 1px solid rgba(255,255,255,0.06);

  .brand-section {
    margin-bottom: 40px;

    .brand-icon {
      width: 72px;
      height: 72px;
      background: linear-gradient(135deg, #4361ee, #7c3aed);
      border-radius: 18px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      margin-bottom: 20px;
      box-shadow: 0 8px 24px rgba(67, 97, 238, 0.4);
    }

    .brand-title {
      font-size: 26px;
      font-weight: 700;
      color: #fff;
      margin-bottom: 10px;
      letter-spacing: 1px;
    }

    .brand-desc {
      font-size: 14px;
      color: rgba(255,255,255,0.5);
      line-height: 1.7;
    }
  }

  .feature-list {
    display: flex;
    flex-direction: column;
    gap: 14px;

    .feature-item {
      display: flex;
      align-items: center;
      gap: 12px;
      color: rgba(255,255,255,0.65);
      font-size: 14px;

      .feature-dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: #4361ee;
        flex-shrink: 0;
        box-shadow: 0 0 8px rgba(67, 97, 238, 0.6);
      }
    }
  }
}

.login-right {
  width: 420px;
  padding: 48px 40px;
  background: #fff;
  display: flex;
  align-items: center;

  .login-card {
    width: 100%;
  }

  .card-header {
    margin-bottom: 32px;

    h2 {
      font-size: 24px;
      font-weight: 700;
      color: #1a1a2e;
      margin-bottom: 8px;
    }

    p {
      font-size: 14px;
      color: #9ca3af;
    }
  }

  :deep(.el-input__wrapper) {
    border-radius: 10px;
    padding: 4px 12px;
    box-shadow: 0 0 0 1px #e5e7eb;
    transition: all 0.3s;

    &:hover, &.is-focus {
      box-shadow: 0 0 0 2px #4361ee;
    }
  }

  .login-btn {
    width: 100%;
    height: 46px;
    border-radius: 10px;
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 4px;
    background: linear-gradient(135deg, #4361ee 0%, #7c3aed 100%);
    border: none;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 6px 20px rgba(67, 97, 238, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .card-footer {
    text-align: center;
    margin-top: 20px;
    font-size: 14px;
    color: #9ca3af;

    .link {
      color: #4361ee;
      text-decoration: none;
      font-weight: 500;
      margin-left: 4px;
      transition: color 0.2s;

      &:hover {
        color: #7c3aed;
      }
    }
  }
}
</style>
