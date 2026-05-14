<template>
  <div>
    <h3>医生工作台</h3>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="8"><el-card><div style="text-align:center"><h1>{{ stats.appointments }}</h1><p>待处理预约</p></div></el-card></el-col>
      <el-col :span="8"><el-card><div style="text-align:center"><h1>{{ stats.consultations }}</h1><p>就诊记录</p></div></el-card></el-col>
      <el-col :span="8"><el-card><div style="text-align:center"><h1>{{ stats.completed }}</h1><p>已完成</p></div></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import request from '../../api'

const stats = reactive({ appointments: 0, consultations: 0, completed: 0 })

onMounted(async () => {
  const [ast, cst] = await Promise.all([
    request.get('/doctor/appointments/stats'),
    request.get('/doctor/consultations/stats')
  ])
  stats.appointments = ast.data.pending
  stats.consultations = cst.data.total
  stats.completed = ast.data.completed
})
</script>
