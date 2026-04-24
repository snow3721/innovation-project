<template>
  <div class="page-container dashboard">
    <div class="welcome-section">
      <div class="welcome-text">
        <h2>{{ greeting }}，{{ userStore.realName }}</h2>
        <p>{{ roleText }} · 今天是 {{ today }}</p>
      </div>
    </div>

    <el-row :gutter="16" class="stat-cards">
      <el-col :span="6">
        <div class="stat-card" style="--accent: #4361ee">
          <div class="stat-icon">
            <el-icon :size="28"><Folder /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ overview.totalProjects || 0 }}</span>
            <span class="stat-label">项目总数</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="--accent: #06d6a0">
          <div class="stat-icon">
            <el-icon :size="28"><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ overview.approvedProjects || 0 }}</span>
            <span class="stat-label">已立项</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="--accent: #ffd166">
          <div class="stat-icon">
            <el-icon :size="28"><Loading /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ overview.runningProjects || 0 }}</span>
            <span class="stat-label">运行中</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="--accent: #ef476f">
          <div class="stat-icon">
            <el-icon :size="28"><Trophy /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ overview.totalAchievements || 0 }}</span>
            <span class="stat-label">成果总数</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <span style="font-weight: 600">项目状态分布</span>
          </template>
          <div ref="chartRef" style="height: 320px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <span style="font-weight: 600">成果类型分布</span>
          </template>
          <div ref="pieRef" style="height: 320px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span style="font-weight: 600">快捷操作</span>
          </template>
          <div class="quick-actions">
            <router-link to="/projects/create" class="action-item">
              <el-icon :size="24"><Plus /></el-icon>
              <span>创建项目</span>
            </router-link>
            <router-link to="/achievements/create" class="action-item">
              <el-icon :size="24"><Medal /></el-icon>
              <span>提交成果</span>
            </router-link>
            <router-link to="/milestones" class="action-item">
              <el-icon :size="24"><Flag /></el-icon>
              <span>里程碑</span>
            </router-link>
            <router-link to="/profile" class="action-item">
              <el-icon :size="24"><Setting /></el-icon>
              <span>个人设置</span>
            </router-link>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span style="font-weight: 600">立项率</span>
          </template>
          <div class="rate-display">
            <div class="rate-circle">
              <span class="rate-num">{{ overview.approvalRate || 0 }}%</span>
            </div>
            <p class="rate-text">当前项目立项通过率</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import * as echarts from 'echarts'
import { useUserStore } from '@/stores/user'
import { getOverview, getByCategory } from '@/api/statistics'
import { Folder, CircleCheck, Loading, Trophy, Plus, Medal, Flag, Setting } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const userStore = useUserStore()
const chartRef = ref<HTMLElement>()
const pieRef = ref<HTMLElement>()
const overview = ref<any>({})
const categoryData = ref<any>({})

const today = dayjs().format('YYYY年MM月DD日')
const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return '早上好'
  if (h < 18) return '下午好'
  return '晚上好'
})
const roleText = computed(() => {
  const map: Record<string, string> = { student: '学生', teacher: '导师', college_admin: '院管理员', school_admin: '校管理员', expert: '评审专家' }
  return map[userStore.role] || ''
})

onMounted(async () => {
  try {
    const res1: any = await getOverview()
    overview.value = res1.data || {}
    const res2: any = await getByCategory()
    categoryData.value = res2.data || {}
  } catch {}

  if (chartRef.value) {
    const chart = echarts.init(chartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['草稿', '待审核', '评审中', '已立项', '运行中', '已结题', '已驳回'] },
      yAxis: { type: 'value' },
      series: [{
        type: 'bar',
        data: [12, 8, 15, 45, 30, 20, 5],
        itemStyle: { borderRadius: [6, 6, 0, 0], color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#4361ee' }, { offset: 1, color: '#7c3aed' }]) }
      }],
      grid: { left: 40, right: 20, top: 20, bottom: 30 }
    })
  }

  if (pieRef.value) {
    const pie = echarts.init(pieRef.value)
    const d = categoryData.value
    pie.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', radius: ['40%', '70%'],
        data: [
          { value: d.patent || 0, name: '专利', itemStyle: { color: '#4361ee' } },
          { value: d.paper || 0, name: '论文', itemStyle: { color: '#06d6a0' } },
          { value: d.software || 0, name: '软著', itemStyle: { color: '#ffd166' } },
          { value: d.competition || 0, name: '竞赛', itemStyle: { color: '#ef476f' } },
          { value: d.business || 0, name: '商业', itemStyle: { color: '#118ab2' } },
        ],
        label: { fontSize: 12 },
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.2)' } }
      }]
    })
  }
})
</script>

<style lang="scss" scoped>
.welcome-section {
  margin-bottom: 20px;
  h2 { font-size: 22px; font-weight: 700; color: var(--text-primary); margin-bottom: 4px; }
  p { color: var(--text-secondary); font-size: 14px; }
}
.stat-cards .stat-card {
  background: #fff; border-radius: var(--radius-md); padding: 20px;
  display: flex; align-items: center; gap: 16px;
  box-shadow: var(--shadow-sm); transition: all 0.3s;
  &:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
  .stat-icon {
    width: 52px; height: 52px; border-radius: 14px;
    background: color-mix(in srgb, var(--accent) 12%, transparent);
    display: flex; align-items: center; justify-content: center; color: var(--accent);
  }
  .stat-info { display: flex; flex-direction: column; }
  .stat-value { font-size: 28px; font-weight: 700; color: var(--text-primary); }
  .stat-label { font-size: 13px; color: var(--text-secondary); margin-top: 2px; }
}
.quick-actions {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px;
  .action-item {
    display: flex; flex-direction: column; align-items: center; justify-content: center;
    padding: 20px 0; border-radius: var(--radius-md); background: #f8fafc;
    text-decoration: none; color: var(--text-primary); gap: 8px;
    transition: all 0.2s; font-size: 13px;
    &:hover { background: #eef2ff; color: var(--primary); }
  }
}
.rate-display {
  display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 20px 0;
  .rate-circle {
    width: 120px; height: 120px; border-radius: 50%;
    background: conic-gradient(var(--primary) 0%, var(--primary) var(--rate, 0%), #e5e7eb var(--rate, 0%), #e5e7eb 100%);
    display: flex; align-items: center; justify-content: center;
    box-shadow: 0 4px 12px rgba(67,97,238,0.2);
    .rate-num { font-size: 28px; font-weight: 700; color: var(--primary); background: #fff; width: 96px; height: 96px; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
  }
  .rate-text { margin-top: 12px; font-size: 14px; color: var(--text-secondary); }
}
</style>
