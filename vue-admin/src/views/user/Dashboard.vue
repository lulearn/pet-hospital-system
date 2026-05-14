<template>
  <div>
    <h3>欢迎使用宠物医院管理系统</h3>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="8"><el-card><div style="text-align:center"><h1>{{ stats.appointments }}</h1><p>我的预约</p></div></el-card></el-col>
      <el-col :span="8"><el-card><div style="text-align:center"><h1>{{ stats.orders }}</h1><p>我的订单</p></div></el-card></el-col>
      <el-col :span="8"><el-card><div style="text-align:center"><h1>¥{{ stats.balance }}</h1><p>账户余额</p></div></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import request from '../../api'
import { useUserStore } from '../../store'

const userStore = useUserStore()
const stats = reactive({ appointments: 0, orders: 0, balance: 0 })

onMounted(async () => {
  const [a, o, p] = await Promise.all([
    request.get('/appointments'),
    request.get('/orders'),
    request.get('/user/profile')
  ])
  stats.appointments = a.data.length
  stats.orders = o.data.length
  stats.balance = p.data.balance || 0
})
</script>
