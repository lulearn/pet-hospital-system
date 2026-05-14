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
      <el-table-column label="状态" width="100">
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

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑就诊记录':'添加就诊记录'" width="700px">
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

      <!-- 药方管理（仅在编辑已有记录时显示） -->
      <div v-if="isEdit" style="margin-top:20px">
        <h4>药方管理</h4>
        <div style="display:flex;gap:10px;margin-bottom:10px">
          <el-select v-model="newMed.medicineId" placeholder="选择药品" style="flex:1">
            <el-option v-for="m in allMedicines" :key="m.id" :label="`${m.name} (¥${m.price}/库存${m.stock})`" :value="m.id" />
          </el-select>
          <el-input-number v-model="newMed.quantity" :min="1" :max="99" style="width:80px" />
          <el-input v-model="newMed.dosage" placeholder="用法用量" style="width:150px" />
          <el-button type="primary" @click="addMedicine">添加</el-button>
        </div>
        <el-table :data="prescriptionMeds" border size="small">
          <el-table-column label="药品">
            <template #default="{row}">{{ medNameMap[row.medicineId] }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="dosage" label="用法用量" />
          <el-table-column label="操作" width="80">
            <template #default="{row}">
              <el-button size="small" type="danger" @click="removeMedicine(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

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
const medNameMap = ref({})
const approvedAppts = ref([])
const allMedicines = ref([])
const selectedApptId = ref(null)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})
const prescriptionMeds = ref([])
const newMed = ref({ medicineId: null, quantity: 1, dosage: '' })

const load = async () => {
  const [consRes, apptRes, userRes, medRes] = await Promise.all([
    request.get('/doctor/consultations'),
    request.get('/doctor/appointments'),
    request.get('/doctor/users'),
    request.get('/medicines')
  ])
  list.value = consRes.data
  approvedAppts.value = apptRes.data.filter(a => a.status === 'APPROVED')
  userRes.data.forEach(u => { userMap.value[u.id] = u.realName })
  allMedicines.value = medRes.data
  medRes.data.forEach(m => { medNameMap.value[m.id] = m.name })
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

const openDialog = async (row) => {
  isEdit.value = !!row?.id
  if (row?.id) {
    form.value = { ...row }
    // 加载已有药方
    const res = await request.get(`/doctor/consultations/${row.id}/medicines`)
    prescriptionMeds.value = res.data
  } else {
    selectedApptId.value = null
    form.value = { userId: null, appointmentId: null, petName: '', diagnosis: '', prescription: '', notes: '' }
    prescriptionMeds.value = []
  }
  newMed.value = { medicineId: null, quantity: 1, dosage: '' }
  dialogVisible.value = true
}

const addMedicine = async () => {
  if (!newMed.value.medicineId) { ElMessage.warning('请选择药品'); return }
  await request.post(`/doctor/consultations/${form.value.id}/medicines`, newMed.value)
  ElMessage.success('药品已添加')
  // 刷新药方列表
  const res = await request.get(`/doctor/consultations/${form.value.id}/medicines`)
  prescriptionMeds.value = res.data
  newMed.value = { medicineId: null, quantity: 1, dosage: '' }
}

const removeMedicine = async (mid) => {
  await request.delete(`/doctor/consultations/${form.value.id}/medicines/${mid}`)
  const res = await request.get(`/doctor/consultations/${form.value.id}/medicines`)
  prescriptionMeds.value = res.data
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
