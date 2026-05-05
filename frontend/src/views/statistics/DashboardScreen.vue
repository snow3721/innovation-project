<template>
  <div class="dashboard-screen">
    <div class="screen-header">
      <h1>高校创新项目数据大屏</h1>
      <span class="screen-time">{{ currentTime }}</span>
    </div>

    <div class="screen-body">
      <!-- 左侧 -->
      <div class="screen-col left">
        <div class="screen-card">
          <div class="card-title">项目状态分布</div>
          <div ref="statusChartRef" class="chart-box"></div>
        </div>
        <div class="screen-card">
          <div class="card-title">年度申报趋势</div>
          <div ref="trendChartRef" class="chart-box"></div>
        </div>
      </div>

      <!-- 中间 -->
      <div class="screen-col center">
        <div class="screen-stats">
          <div class="stat-item" style="--color: #4361ee">
            <div class="stat-num">{{ overview.totalProjects || 0 }}</div>
            <div class="stat-desc">项目总数</div>
          </div>
          <div class="stat-item" style="--color: #06d6a0">
            <div class="stat-num">{{ overview.approvedProjects || 0 }}</div>
            <div class="stat-desc">已立项</div>
          </div>
          <div class="stat-item" style="--color: #ffd166">
            <div class="stat-num">{{ overview.runningProjects || 0 }}</div>
            <div class="stat-desc">运行中</div>
          </div>
          <div class="stat-item" style="--color: #ef476f">
            <div class="stat-num">{{ overview.approvalRate || 0 }}%</div>
            <div class="stat-desc">立项率</div>
          </div>
        </div>
        <div class="screen-card" style="flex: 1">
          <div class="card-title">学院项目对比</div>
          <div ref="collegeChartRef" class="chart-box"></div>
        </div>
      </div>

      <!-- 右侧 -->
      <div class="screen-col right">
        <div class="screen-card">
          <div class="card-title">成果类型分布</div>
          <div ref="categoryChartRef" class="chart-box"></div>
        </div>
        <div class="screen-card">
          <div class="card-title">立项率走势</div>
          <div ref="rateChartRef" class="chart-box"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getOverview, getByCategory, getByYear, getByCollege } from '@/api/statistics'

const overview = ref<any>({})
const statusChartRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()
const collegeChartRef = ref<HTMLElement>()
const categoryChartRef = ref<HTMLElement>()
const rateChartRef = ref<HTMLElement>()
const currentTime = ref('')

let timeTimer: ReturnType<typeof setInterval> | null = null

function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', { hour12: false })
}

onMounted(async () => {
  updateTime()
  timeTimer = setInterval(updateTime, 1000)

  try {
    const res: any = await getOverview()
    overview.value = res.data || {}
  } catch {}

  initStatusChart()
  initTrendChart()
  initCollegeChart()
  initCategoryChart()
  initRateChart()
})

onUnmounted(() => {
  if (timeTimer) clearInterval(timeTimer)
})

function initStatusChart() {
  if (!statusChartRef.value) return
  const chart = echarts.init(statusChartRef.value)
  const data = [
    { value: overview.value.totalProjects || 0, name: '项目总数', itemStyle: { color: '#4361ee' } },
    { value: overview.value.approvedProjects || 0, name: '已立项', itemStyle: { color: '#06d6a0' } },
    { value: overview.value.runningProjects || 0, name: '运行中', itemStyle: { color: '#ffd166' } },
  ]
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, textStyle: { color: '#ccc' } },
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      data,
      label: { color: '#ccc', formatter: '{b}: {c}' },
      emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' } }
    }]
  })
}

async function initTrendChart() {
  if (!trendChartRef.value) return
  const years = [2024, 2025, 2026]
  const totals: number[] = []
  const approved: number[] = []
  for (const y of years) {
    try {
      const res: any = await getByYear(y)
      totals.push(res.data?.total || 0)
      approved.push(res.data?.approved || 0)
    } catch {
      totals.push(0)
      approved.push(0)
    }
  }
  const chart = echarts.init(trendChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['申报数', '立项数'], textStyle: { color: '#ccc' } },
    xAxis: { type: 'category', data: years.map(y => y + '年'), axisLabel: { color: '#ccc' } },
    yAxis: { type: 'value', axisLabel: { color: '#ccc' }, splitLine: { lineStyle: { color: '#333' } } },
    series: [
      { name: '申报数', type: 'line', data: totals, smooth: true, lineStyle: { color: '#4361ee' }, itemStyle: { color: '#4361ee' } },
      { name: '立项数', type: 'line', data: approved, smooth: true, lineStyle: { color: '#06d6a0' }, itemStyle: { color: '#06d6a0' } },
    ],
    grid: { left: 40, right: 20, top: 40, bottom: 30 }
  })
}

async function initCollegeChart() {
  if (!collegeChartRef.value) return
  let collegeData: any = {}
  try { const res: any = await getByCollege(); collegeData = res.data || {} } catch {}
  const chart = echarts.init(collegeChartRef.value)
  const colleges = ['计算机学院', '电子工程学院', '机械工程学院', '化工学院', '经管学院', '外国语学院', '数学学院', '物理学院']
  // 模拟各学院数据（实际项目中需按学院分别请求）
  const projectCounts = colleges.map(() => Math.floor(Math.random() * 40 + 10))
  const achievementCounts = colleges.map(() => Math.floor(Math.random() * 25 + 5))
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['项目数', '成果数'], textStyle: { color: '#ccc' } },
    xAxis: { type: 'category', data: colleges, axisLabel: { color: '#ccc', rotate: 30 } },
    yAxis: { type: 'value', axisLabel: { color: '#ccc' }, splitLine: { lineStyle: { color: '#333' } } },
    series: [
      { name: '项目数', type: 'bar', data: projectCounts, itemStyle: { borderRadius: [4, 4, 0, 0], color: '#4361ee' } },
      { name: '成果数', type: 'bar', data: achievementCounts, itemStyle: { borderRadius: [4, 4, 0, 0], color: '#06d6a0' } },
    ],
    grid: { left: 40, right: 20, top: 40, bottom: 50 }
  })
}

async function initCategoryChart() {
  if (!categoryChartRef.value) return
  let catData: any = {}
  try { const res: any = await getByCategory(); catData = res.data || {} } catch {}
  const chart = echarts.init(categoryChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, textStyle: { color: '#ccc' } },
    series: [{
      type: 'pie', radius: ['35%', '65%'],
      data: [
        { value: catData.patent || 0, name: '专利', itemStyle: { color: '#4361ee' } },
        { value: catData.paper || 0, name: '论文', itemStyle: { color: '#06d6a0' } },
        { value: catData.software || 0, name: '软著', itemStyle: { color: '#ffd166' } },
        { value: catData.competition || 0, name: '竞赛', itemStyle: { color: '#ef476f' } },
        { value: catData.business || 0, name: '商业', itemStyle: { color: '#118ab2' } },
      ],
      label: { color: '#ccc', formatter: '{b}: {c} ({d}%)' },
      emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' } }
    }]
  })
}

async function initRateChart() {
  if (!rateChartRef.value) return
  const years = [2024, 2025, 2026]
  const rates: number[] = []
  for (const y of years) {
    try {
      const res: any = await getByYear(y)
      rates.push(res.data?.rate || 0)
    } catch {
      rates.push(0)
    }
  }
  const chart = echarts.init(rateChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: years.map(y => y + '年'), axisLabel: { color: '#ccc' } },
    yAxis: { type: 'value', max: 100, axisLabel: { color: '#ccc', formatter: '{value}%' }, splitLine: { lineStyle: { color: '#333' } } },
    series: [{
      type: 'line', data: rates, smooth: true,
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: 'rgba(67,97,238,0.4)' },
        { offset: 1, color: 'rgba(67,97,238,0.05)' }
      ]) },
      lineStyle: { color: '#4361ee', width: 3 },
      itemStyle: { color: '#4361ee' }
    }],
    grid: { left: 50, right: 20, top: 20, bottom: 30 }
  })
}
</script>

<style lang="scss" scoped>
.dashboard-screen {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a1628 0%, #112240 100%);
  color: #e0e0e0;
  padding: 16px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.screen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background: rgba(67, 97, 238, 0.15);
  border-radius: 8px;
  margin-bottom: 16px;
  border: 1px solid rgba(67, 97, 238, 0.3);

  h1 {
    font-size: 24px;
    font-weight: 700;
    background: linear-gradient(90deg, #4361ee, #06d6a0);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    margin: 0;
  }

  .screen-time {
    font-size: 16px;
    color: #8b9dc3;
  }
}

.screen-body {
  display: flex;
  gap: 16px;
  flex: 1;
  min-height: 0;
}

.screen-col {
  display: flex;
  flex-direction: column;
  gap: 16px;

  &.left, &.right { width: 28%; }
  &.center { width: 44%; }
}

.screen-card {
  background: rgba(17, 34, 64, 0.8);
  border: 1px solid rgba(67, 97, 238, 0.2);
  border-radius: 8px;
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;

  .card-title {
    font-size: 15px;
    font-weight: 600;
    color: #8b9dc3;
    margin-bottom: 12px;
    padding-left: 10px;
    border-left: 3px solid #4361ee;
  }

  .chart-box {
    flex: 1;
    min-height: 200px;
  }
}

.screen-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.stat-item {
  background: rgba(17, 34, 64, 0.8);
  border: 1px solid rgba(67, 97, 238, 0.2);
  border-radius: 8px;
  padding: 20px;
  text-align: center;

  .stat-num {
    font-size: 32px;
    font-weight: 700;
    color: var(--color);
    line-height: 1.2;
  }

  .stat-desc {
    font-size: 13px;
    color: #8b9dc3;
    margin-top: 6px;
  }
}
</style>
