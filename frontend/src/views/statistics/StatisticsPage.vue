<template>
  <div class="page-container">
    <div class="page-header"><h2>数据统计</h2></div>

    <el-row :gutter="16">
      <el-col :span="6">
        <div class="stat-card" style="--accent: #4361ee">
          <div class="stat-icon"><el-icon :size="28"><Folder /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ overview.totalProjects || 0 }}</span>
            <span class="stat-label">项目总数</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="--accent: #06d6a0">
          <div class="stat-icon"><el-icon :size="28"><CircleCheck /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ overview.approvedProjects || 0 }}</span>
            <span class="stat-label">已立项</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="--accent: #ffd166">
          <div class="stat-icon"><el-icon :size="28"><Trophy /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ overview.totalAchievements || 0 }}</span>
            <span class="stat-label">成果总数</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="--accent: #ef476f">
          <div class="stat-icon"><el-icon :size="28"><TrendCharts /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ overview.approvalRate || 0 }}%</span>
            <span class="stat-label">立项率</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span style="font-weight:600">成果类型分布</span></template>
          <div ref="categoryChartRef" style="height: 320px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-weight:600">年度趋势</span>
              <el-select v-model="selectedYear" size="small" style="width: 100px" @change="loadYearData">
                <el-option v-for="y in [2024, 2025, 2026]" :key="y" :label="y + '年'" :value="y" />
              </el-select>
            </div>
          </template>
          <div ref="yearChartRef" style="height: 320px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-weight:600">学院项目对比</span>
            </div>
          </template>
          <div ref="collegeChartRef" style="height: 360px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { Folder, CircleCheck, Trophy, TrendCharts } from '@element-plus/icons-vue'
import { getOverview, getByCategory, getByYear, getByCollege } from '@/api/statistics'

const overview = ref<any>({})
const categoryChartRef = ref<HTMLElement>()
const yearChartRef = ref<HTMLElement>()
const collegeChartRef = ref<HTMLElement>()
const selectedYear = ref(2026)

onMounted(async () => {
  try {
    const res: any = await getOverview()
    overview.value = res.data || {}
  } catch {}

  if (categoryChartRef.value) {
    let catData: any = {}
    try { const res: any = await getByCategory(); catData = res.data || {} } catch {}
    const chart = echarts.init(categoryChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie', radius: ['35%', '65%'],
        data: [
          { value: catData.patent || 0, name: '专利', itemStyle: { color: '#4361ee' } },
          { value: catData.paper || 0, name: '论文', itemStyle: { color: '#06d6a0' } },
          { value: catData.software || 0, name: '软著', itemStyle: { color: '#ffd166' } },
          { value: catData.competition || 0, name: '竞赛', itemStyle: { color: '#ef476f' } },
          { value: catData.business || 0, name: '商业', itemStyle: { color: '#118ab2' } },
        ],
        emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.2)' } },
        label: { formatter: '{b}: {c} ({d}%)' }
      }]
    })
  }

  loadYearData()
  loadCollegeData()
})

async function loadYearData() {
  if (!yearChartRef.value) return
  let yearData: any = {}
  try { const res: any = await getByYear(selectedYear.value); yearData = res.data || {} } catch {}
  const chart = echarts.init(yearChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['申报数', '立项数', '立项率(%)'] },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: [yearData.total || 0, yearData.approved || 0, yearData.rate || 0],
      itemStyle: {
        borderRadius: [6, 6, 0, 0],
        color: (params: any) => ['#4361ee', '#06d6a0', '#ffd166'][params.dataIndex]
      }
    }],
    grid: { left: 40, right: 20, top: 20, bottom: 30 }
  })
}

async function loadCollegeData() {
  if (!collegeChartRef.value) return
  const chart = echarts.init(collegeChartRef.value)
  const colleges = ['计算机学院', '电子工程学院', '机械工程学院', '化工学院', '经管学院', '外国语学院', '数学学院', '物理学院']
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['项目数', '成果数'] },
    xAxis: { type: 'category', data: colleges },
    yAxis: { type: 'value' },
    series: [
      { name: '项目数', type: 'bar', data: [45, 32, 28, 25, 38, 15, 20, 18], itemStyle: { borderRadius: [4, 4, 0, 0], color: '#4361ee' } },
      { name: '成果数', type: 'bar', data: [30, 22, 18, 15, 25, 8, 12, 10], itemStyle: { borderRadius: [4, 4, 0, 0], color: '#06d6a0' } }
    ],
    grid: { left: 40, right: 20, top: 40, bottom: 30 }
  })
}
</script>

<style lang="scss" scoped>
.stat-card {
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
</style>
