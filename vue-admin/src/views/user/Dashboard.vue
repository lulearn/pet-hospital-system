<template>
  <div>
    <h3>欢迎使用宠物医院管理系统</h3>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6"><el-card><div style="text-align:center"><h1>{{ stats.appointments }}</h1><p>我的预约</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div style="text-align:center"><h1>{{ stats.orders }}</h1><p>我的订单</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div style="text-align:center"><h1>¥{{ stats.balance }}</h1><p>账户余额</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div style="text-align:center"><h1>{{ stats.unread }}</h1><p>未读消息</p></div></el-card></el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12"><el-card><div ref="apptChart" style="height:300px" /></el-card></el-col>
      <el-col :span="12"><el-card><div ref="orderChart" style="height:300px" /></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '../../api'
import { useUserStore } from '../../store'

const userStore = useUserStore()
const stats = reactive({ appointments: 0, orders: 0, balance: 0, unread: 0 })
const apptChart = ref()
const orderChart = ref()

onMounted(async () => {
  const [a, o, p, n] = await Promise.all([
    request.get('/appointments'),
    request.get('/orders'),
    request.get('/user/profile'),
    request.get('/notifications/unread-count')
  ])
  stats.appointments = a.data.length
  stats.orders = o.data.length
  stats.balance = p.data.balance || 0
  stats.unread = n.data.count

  await nextTick()
  // 预约状态分布饼图
  const apptStatus = {}
  a.data.forEach(x => { apptStatus[x.status] = (apptStatus[x.status] || 0) + 1 })
  const apptInst = echarts.init(apptChart.value)
  apptInst.setOption({
    title: { text: '预约状态分布', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: '60%',
      data: Object.entries(apptStatus).map(([k, v]) => ({ name: statusMap[k] || k, value: v }))
    }]
  })

  // 订单状态柱状图
  const orderStatus = {}
  o.data.forEach(x => { orderStatus[x.status] = (orderStatus[x.status] || 0) + 1 })
  const orderInst = echarts.init(orderChart.value)
  orderInst.setOption({
    title: { text: '订单状态', left: 'center' },
    tooltip: {},
    xAxis: { data: Object.keys(orderStatus).map(k => orderStatusMap[k] || k) },
    yAxis: { minInterval: 1 },
    series: [{ type: 'bar', data: Object.values(orderStatus), itemStyle: { color: '#409EFF' } }]
  })
})

const statusMap = { PENDING: '待处理', APPROVED: '已通过', REJECTED: '已拒绝', IN_PROGRESS: '就诊中', COMPLETED: '已完成' }
const orderStatusMap = { UNPAID: '未支付', PAID: '已支付', SHIPPED: '已发货', RECEIVED: '已收货' }
</script>
