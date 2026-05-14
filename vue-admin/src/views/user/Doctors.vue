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
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="预约医生" width="500px">
      <el-form :model="form">
        <el-form-item label="医生">{{ selectedDoctor?.realName }}</el-form-item>
        <el-form-item label="宠物名称"><el-input v-model="form.petName" /></el-form-item>
        <el-form-item label="宠物种类"><el-input v-model="form.species" /></el-form-item>
        <el-form-item label="预约时间"><el-date-picker v-model="form.appointmentTime" type="datetime" /></el-form-item>
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

const doctors = ref([])
const dialogVisible = ref(false)
const selectedDoctor = ref(null)
const form = ref({})

onMounted(async () => {
  const res = await request.get('/doctors')
  doctors.value = res.data
})

const openDialog = (doctor) => {
  selectedDoctor.value = doctor
  form.value = { doctorId: doctor.id, petName: '', species: '', description: '', appointmentTime: '' }
  dialogVisible.value = true
}

const submit = async () => {
  await request.post('/appointments', form.value)
  ElMessage.success('预约成功')
  dialogVisible.value = false
}
</script>
