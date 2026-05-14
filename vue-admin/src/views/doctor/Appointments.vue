<template>
  <div>
    <h3>预约管理</h3>
    <el-table :data="list" border stripe>
      <el-table-column label="用户名" width="100">
        <template #default="{row}">{{ userMap[row.userId] || row.userId }}</template>
      </el-table-column>
      <el-table-column prop="petName" label="宠物名" />
      <el-table-column prop="species" label="种类" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="appointmentTime" label="预约时间" width="160" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="row.status==='APPROVED'?'success':row.status==='REJECTED'?'danger':row.status==='PENDING'?'warning':row.status==='IN_PROGRESS'?'':row.status==='COMPLETED'?'success':'info'">
            {{ {PENDING:'待审核',APPROVED:'已通过',REJECTED:'已拒绝',IN_PROGRESS:'就诊中',COMPLETED:'已完成'}[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{row}">
          <el-button v-if="row.status==='PENDING'" size="small" type="success" @click="approve(row)">通过</el-button>
          <el-button v-if="row.status==='PENDING'" size="small" type="danger" @click="reject(row)">拒绝</el-button>
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="编辑预约" width="500px">
      <el-form :model="form">
        <el-form-item label="宠物名称"><el-input v-model="form.petName" /></el-form-item>
        <el-form-item label="宠物种类"><el-input v-model="form.species" /></el-form-item>
        <el-form-item label="预约时间"><el-date-picker v-model="form.appointmentTime" type="datetime" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api'
import { ElMessage } from 'element-plus'

const list = ref([])
const userMap = ref({})
const dialogVisible = ref(false)
const form = ref({})

const load = async () => {
  const [apptRes, userRes] = await Promise.all([
    request.get('/doctor/appointments'),
    request.get('/doctor/users')
  ])
  list.value = apptRes.data
  userRes.data.forEach(u => { userMap.value[u.id] = u.realName })
}
onMounted(load)

const approve = async (row) => { row.status = 'APPROVED'; await request.put('/doctor/appointments', row); ElMessage.success('已通过'); load() }
const reject = async (row) => { row.status = 'REJECTED'; await request.put('/doctor/appointments', row); ElMessage.success('已拒绝'); load() }

const openDialog = (row) => { form.value = { ...row }; dialogVisible.value = true }
const save = async () => { await request.put('/doctor/appointments', form.value); ElMessage.success('保存成功'); dialogVisible.value = false; load() }
</script>
