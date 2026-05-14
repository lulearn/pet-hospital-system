<template>
  <div>
    <h3>医生工作台</h3>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6"><el-card><div style="text-align:center"><h1>{{ stats.pending }}</h1><p>待处理预约</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div style="text-align:center"><h1>{{ stats.total }}</h1><p>就诊记录</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div style="text-align:center"><h1>{{ stats.completed }}</h1><p>已完成</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div style="text-align:center"><h1>{{ stats.todaySchedules }}</h1><p>今日排班</p></div></el-card></el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12"><el-card><div ref="apptChart" style="height:300px" /></el-card></el-col>
      <el-col :span="12"><el-card><div ref="ratingChart" style="height:300px" /></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '../../api'

const stats = reactive({ pending: 0, total: 0, completed: 0, todaySchedules: 0 })
const apptChart = ref()
const ratingChart = ref()

onMounted(async () => {
  const [ast, cst, sch] = await Promise.all([
    request.get('/doctor/appointments/stats'),
    request.get('/doctor/consultations/stats'),
    request.get('/doctor/schedules')
  ])
  stats.pending = ast.data.pending
  stats.total = cst.data.total
  stats.completed = ast.data.completed
  // 今日排班: 根据今天是周几
  const today = new Date().getDay()
  const dayMap = { 0: 7, 1: 1, 2: 2, 3: 3, 4: 4, 5: 5, 6: 6 }
  stats.todaySchedules = sch.data.filter(s => s.dayOfWeek === dayMap[today]).length

  await nextTick()

  // 获取完整预约数据用于图表
  const appts = await request.get('/doctor/appointments')
  const statusCount = {}
  appts.data.forEach(a => { statusCount[a.status] = (statusCount[a.status] || 0) + 1 })
  const statusMap = { PENDING: '待处理', APPROVED: '已通过', REJECTED: '已拒绝', IN_PROGRESS: '就诊中', COMPLETED: '已完成' }
  const apptInst = echarts.init(apptChart.value)
  apptInst.setOption({
    title: { text: '预约状态分布', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      data: Object.entries(statusCount).map(([k, v]) => ({ name: statusMap[k] || k, value: v }))
    }]
  })

  // 评分分布
  const consultations = await request.get('/doctor/consultations')
  const ratings = consultations.data.filter(c => c.rating).map(c => c.rating)
  const ratingDist = [0, 0, 0, 0, 0]
  ratings.forEach(r => { ratingDist[r - 1]++ })
  const ratingInst = echarts.init(ratingChart.value)
  ratingInst.setOption({
    title: { text: '评分分布', left: 'center' },
    tooltip: {},
    xAxis: { data: ['1星', '2星', '3星', '4星', '5星'] },
    yAxis: { minInterval: 1 },
    series: [{
      type: 'bar', data: ratingDist,
      itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#67C23A' }, { offset: 1, color: '#409EFF' }]) }
    }]
  })
})
</script>
