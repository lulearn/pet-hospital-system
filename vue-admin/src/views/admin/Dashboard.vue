<template>
  <div>
    <h3>管理员工作台</h3>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="4"><el-card><div style="text-align:center"><h1>{{ stats.userCount }}</h1><p>用户总数</p></div></el-card></el-col>
      <el-col :span="4"><el-card><div style="text-align:center"><h1>{{ stats.doctorCount }}</h1><p>医生总数</p></div></el-card></el-col>
      <el-col :span="4"><el-card><div style="text-align:center"><h1>{{ stats.medicineCount }}</h1><p>药品种类</p></div></el-card></el-col>
      <el-col :span="4"><el-card><div style="text-align:center"><h1>{{ stats.orderCount }}</h1><p>订单总数</p></div></el-card></el-col>
      <el-col :span="4"><el-card><div style="text-align:center"><h1>{{ stats.pendingCount }}</h1><p>待处理预约</p></div></el-card></el-col>
      <el-col :span="4"><el-card><div style="text-align:center"><h1>¥{{ stats.monthRevenue }}</h1><p>本月收入</p></div></el-card></el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12"><el-card><div ref="revenueChart" style="height:300px" /></el-card></el-col>
      <el-col :span="12"><el-card><div ref="userChart" style="height:300px" /></el-card></el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="24">
        <el-card>
          <template #header>数据导出</template>
          <el-button type="primary" @click="exportAppointments">导出预约记录</el-button>
          <el-button type="success" @click="exportOrders" style="margin-left:20px">导出订单记录</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '../../api'
import { ElMessage } from 'element-plus'

const stats = reactive({ userCount: 0, doctorCount: 0, medicineCount: 0, orderCount: 0, pendingCount: 0, monthRevenue: 0 })
const revenueChart = ref()
const userChart = ref()

onMounted(async () => {
  const [dash, rev] = await Promise.all([
    request.get('/admin/stats/dashboard'),
    request.get('/admin/stats/revenue')
  ])
  Object.assign(stats, dash.data)
  stats.monthRevenue = dash.data.monthRevenue || 0

  await nextTick()

  // 收入分布
  const revInst = echarts.init(revenueChart.value)
  revInst.setOption({
    title: { text: '订单收入统计', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: '60%',
      data: [
        { name: '已支付', value: rev.data.paidCount },
        { name: '已发货', value: rev.data.shippedCount },
        { name: '已收货', value: rev.data.receivedCount }
      ],
      label: { formatter: '{b}: {c} 单' }
    }]
  })

  // 用户/医生柱状图
  const userInst = echarts.init(userChart.value)
  userInst.setOption({
    title: { text: '平台数据概览', left: 'center' },
    tooltip: {},
    xAxis: { data: ['用户', '医生', '药品', '订单', '待处理预约'] },
    yAxis: { minInterval: 1 },
    series: [{
      type: 'bar', barWidth: '50%',
      data: [
        { value: stats.userCount, itemStyle: { color: '#409EFF' } },
        { value: stats.doctorCount, itemStyle: { color: '#67C23A' } },
        { value: stats.medicineCount, itemStyle: { color: '#E6A23C' } },
        { value: stats.orderCount, itemStyle: { color: '#F56C6C' } },
        { value: stats.pendingCount, itemStyle: { color: '#909399' } }
      ]
    }]
  })
})

const exportAppointments = async () => {
  const res = await request.get('/admin/export/appointments')
  downloadCsv(res.data, '预约记录')
}

const exportOrders = async () => {
  const res = await request.get('/admin/export/orders')
  downloadCsv(res.data, '订单记录')
}

const downloadCsv = (data, name) => {
  if (!data.length) { ElMessage.warning('无数据可导出'); return }
  const headers = Object.keys(data[0])
  const csv = [headers.join(','), ...data.map(row => headers.map(h => '"' + (row[h] || '') + '"').join(','))].join('\n')
  const b = new Blob(['﻿' + csv], { type: 'text/csv;charset=utf-8' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(b)
  a.download = `${name}_${new Date().toISOString().slice(0, 10)}.csv`
  a.click()
  ElMessage.success('导出成功')
}
</script>
