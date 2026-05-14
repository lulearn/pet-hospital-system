<template>
  <div>
    <h3>就诊管理</h3>
    <el-button type="primary" @click="openDialog()" style="margin-bottom:15px">添加就诊记录</el-button>
    <el-table :data="list" border stripe>
      <el-table-column label="用户名" width="100">
        <template #default="{row}">{{ userMap[row.userId] || row.userId }}</template>
      </el-table-column>
      <el-table-column prop="petName" label="宠物名" width="100" />
      <el-table-column prop="diagnosis" label="诊断结果" />
      <el-table-column prop="prescription" label="处方" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="row.status==='COMPLETED'?'success':'warning'">
            {{ row.status==='COMPLETED'?'已完成':'就诊中' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="就诊时间" width="160" />
      <el-table-column label="操作" width="280">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button v-if="row.status!=='COMPLETED'" size="small" type="success" @click="complete(row)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑就诊记录':'添加就诊记录'" width="600px">
      <el-form :model="form">
        <el-form-item v-if="!isEdit" label="选择预约">
          <el-select v-model="selectedApptId" placeholder="请选择已通过的预约" @change="onApptSelect" style="width:100%">
            <el-option v-for="a in approvedAppts" :key="a.id" :label="`${a.petName} - ${userMap[a.userId] || a.userId}`" :value="a.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="宠物名称"><el-input v-model="form.petName" /></el-form-item>
        <el-form-item label="诊断结果"><el-input v-model="form.diagnosis" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="处方"><el-input v-model="form.prescription" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.notes" type="textarea" :rows="2" /></el-form-item>
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
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const userMap = ref({})
const approvedAppts = ref([])
const selectedApptId = ref(null)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const load = async () => {
  const [consRes, apptRes, userRes] = await Promise.all([
    request.get('/doctor/consultations'),
    request.get('/doctor/appointments'),
    request.get('/doctor/users')
  ])
  list.value = consRes.data
  approvedAppts.value = apptRes.data.filter(a => a.status === 'APPROVED')
  userRes.data.forEach(u => { userMap.value[u.id] = u.realName })
}
onMounted(load)

const onApptSelect = (apptId) => {
  const appt = approvedAppts.value.find(a => a.id === apptId)
  if (appt) {
    form.value.userId = appt.userId
    form.value.appointmentId = appt.id
    form.value.petName = appt.petName
  }
}

const openDialog = (row) => {
  isEdit.value = !!row?.id
  if (row?.id) {
    form.value = { ...row }
  } else {
    selectedApptId.value = null
    form.value = { userId: null, appointmentId: null, petName: '', diagnosis: '', prescription: '', notes: '' }
  }
  dialogVisible.value = true
}

const complete = async (row) => {
  await ElMessageBox.confirm('确认完成就诊？患者已离开', '提示', { type: 'warning' })
  await request.put(`/doctor/consultations/${row.id}/complete`)
  ElMessage.success('就诊已完成')
  load()
}

const save = async () => {
  if (isEdit.value) await request.put('/doctor/consultations', form.value)
  else await request.post('/doctor/consultations', form.value)
  ElMessage.success('保存成功'); dialogVisible.value = false; load()
}
</script>
