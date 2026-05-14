<template>
  <div>
    <h3>就诊记录</h3>
    <el-table :data="list" border stripe>
      <el-table-column label="医生" width="100">
        <template #default="{row}">{{ doctorMap[row.doctorId] || row.doctorId }}</template>
      </el-table-column>
      <el-table-column prop="petName" label="宠物名" width="100" />
      <el-table-column prop="diagnosis" label="诊断结果" />
      <el-table-column prop="prescription" label="处方" />
      <el-table-column prop="notes" label="备注" />
      <el-table-column prop="createTime" label="就诊时间" width="160" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api'

const list = ref([])
const doctorMap = ref({})

onMounted(async () => {
  const [consRes, docRes] = await Promise.all([
    request.get('/consultations'),
    request.get('/doctors')
  ])
  list.value = consRes.data
  docRes.data.forEach(d => { doctorMap.value[d.id] = d.realName })
})
</script>
