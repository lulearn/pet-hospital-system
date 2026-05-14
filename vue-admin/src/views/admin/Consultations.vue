<template>
  <div>
    <h3>就诊记录管理</h3>
    <el-table :data="list" border stripe>
      <el-table-column label="用户" width="100">
        <template #default="{row}">{{ userMap[row.userId] || row.userId }}</template>
      </el-table-column>
      <el-table-column label="医生" width="100">
        <template #default="{row}">{{ doctorMap[row.doctorId] || row.doctorId }}</template>
      </el-table-column>
      <el-table-column prop="petName" label="宠物名" width="100" />
      <el-table-column prop="diagnosis" label="诊断结果" />
      <el-table-column prop="prescription" label="处方" />
      <el-table-column label="状态" width="130">
        <template #default="{row}">
          <el-tag v-if="row.deleted===1" type="danger">已删除</el-tag>
          <el-tag v-else :type="row.status==='COMPLETED'?'success':'warning'">
            {{ row.status==='COMPLETED'?'已完成':'就诊中' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="就诊时间" width="160" />
      <el-table-column label="操作" width="100">
        <template #default="{row}">
          <el-button v-if="row.deleted!==1" size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const userMap = ref({})
const doctorMap = ref({})

const load = async () => {
  const [consRes, userRes, docRes] = await Promise.all([
    request.get('/admin/consultations'),
    request.get('/admin/users'),
    request.get('/admin/doctors')
  ])
  list.value = consRes.data
  userRes.data.forEach(u => { userMap.value[u.id] = u.realName })
  docRes.data.forEach(d => { doctorMap.value[d.id] = d.realName })
}
onMounted(load)

const del = async (id) => {
  await ElMessageBox.confirm('确定删除该就诊记录？不会影响医生端统计数据', '提示', { type: 'warning' })
  await request.delete(`/admin/consultations/${id}`)
  ElMessage.success('删除成功')
  load()
}
</script>
