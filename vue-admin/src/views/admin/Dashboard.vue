<template>
  <div>
    <h3>管理员工作台</h3>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6"><el-card><div style="text-align:center"><h1>{{ stats.users }}</h1><p>用户总数</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div style="text-align:center"><h1>{{ stats.doctors }}</h1><p>医生总数</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div style="text-align:center"><h1>{{ stats.medicines }}</h1><p>药品种类</p></div></el-card></el-col>
      <el-col :span="6"><el-card><div style="text-align:center"><h1>{{ stats.orders }}</h1><p>订单总数</p></div></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import request from '../../api'

const stats = reactive({ users: 0, doctors: 0, medicines: 0, orders: 0 })

onMounted(async () => {
  const [u, d, m, o] = await Promise.all([
    request.get('/admin/users'), request.get('/admin/doctors'),
    request.get('/admin/medicines'), request.get('/admin/orders')
  ])
  stats.users = u.data.length
  stats.doctors = d.data.length
  stats.medicines = m.data.length
  stats.orders = o.data.length
})
</script>
