<template>
  <div>
    <h3>医生列表 - 就诊预约</h3>
    <el-row :gutter="20">
      <el-col :span="8" v-for="d in doctors" :key="d.id" style="margin-top:20px">
        <el-card>
          <h4>{{ d.realName }} <el-tag size="small">{{ d.specialty }}</el-tag></h4>
          <p>{{ d.description }}</p>
          <p>电话: {{ d.phone }}</p>
          <el-button type="primary" @click="openDialog(d)">预约</el-button>
          <el-button size="small" @click="showSchedule(d)">查看排班</el-button>
        </el-card>
      </el-col>
    </el-row>

    <!-- 排班弹窗 -->
    <el-dialog v-model="scheduleDialog" :title="scheduleDoctor?.realName + ' 排班表'" width="500px">
      <el-table :data="schedules" border>
        <el-table-column label="星期">
          <template #default="{row}">{{ weekMap[row.dayOfWeek] }}</template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="120" />
        <el-table-column prop="endTime" label="结束时间" width="120" />
        <el-table-column prop="maxAppointments" label="最大接诊数" width="120" />
      </el-table>
      <div v-if="!schedules.length" style="text-align:center;color:#999;padding:20px">暂无排班信息</div>
    </el-dialog>

    <!-- 预约弹窗 -->
    <el-dialog v-model="dialogVisible" title="预约医生" width="500px">
      <el-form :model="form">
        <el-form-item label="医生">{{ selectedDoctor?.realName }}</el-form-item>
        <el-form-item label="选择宠物">
          <el-select v-model="form.petId" placeholder="请选择您的宠物" style="width:100%">
            <el-option v-for="p in pets" :key="p.id" :label="`${p.name} (${p.species})`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="宠物名称"><el-input v-model="form.petName" placeholder="未选择宠物时手动输入" /></el-form-item>
        <el-form-item label="宠物种类"><el-input v-model="form.species" /></el-form-item>
        <el-form-item label="预约时间"><el-date-picker v-model="form.appointmentTime" type="datetime" style="width:100%" /></el-form-item>
        <el-form-item label="病情描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api'
import { ElMessage } from 'element-plus'

const weekMap = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五', 6: '周六', 7: '周日' }
const doctors = ref([])
const pets = ref([])
const schedules = ref([])
const dialogVisible = ref(false)
const scheduleDialog = ref(false)
const selectedDoctor = ref(null)
const scheduleDoctor = ref(null)
const form = ref({})

onMounted(async () => {
  const [docRes, petRes] = await Promise.all([
    request.get('/doctors'),
    request.get('/pets')
  ])
  doctors.value = docRes.data
  pets.value = petRes.data
})

const showSchedule = async (doctor) => {
  scheduleDoctor.value = doctor
  const res = await request.get(`/doctor-schedules/${doctor.id}`)
  schedules.value = res.data
  scheduleDialog.value = true
}

const openDialog = (doctor) => {
  selectedDoctor.value = doctor
  form.value = { doctorId: doctor.id, petId: null, petName: '', species: '', description: '', appointmentTime: '' }
  dialogVisible.value = true
}

const submit = async () => {
  // 如果选了宠物，自动填充名称和种类
  if (form.value.petId) {
    const pet = pets.value.find(p => p.id === form.value.petId)
    if (pet) {
      form.value.petName = pet.name
      form.value.species = pet.species
    }
  }
  await request.post('/appointments', form.value)
  ElMessage.success('预约成功')
  dialogVisible.value = false
}
</script>
