<template>
  <div>
    <h3>排班管理</h3>
    <el-button type="primary" @click="openDialog()" style="margin-bottom:15px">添加排班</el-button>
    <el-table :data="list" border stripe>
      <el-table-column label="星期" width="100">
        <template #default="{row}">{{ weekMap[row.dayOfWeek] }}</template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="120" />
      <el-table-column prop="endTime" label="结束时间" width="120" />
      <el-table-column prop="maxAppointments" label="最大接诊数" width="120" />
      <el-table-column label="操作" width="100">
        <template #default="{row}">
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="添加排班" width="400px">
      <el-form :model="form">
        <el-form-item label="星期">
          <el-select v-model="form.dayOfWeek" style="width:100%">
            <el-option v-for="(v,k) in weekMap" :key="k" :label="v" :value="parseInt(k)" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间"><el-time-picker v-model="form.startTime" format="HH:mm" value-format="HH:mm" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间"><el-time-picker v-model="form.endTime" format="HH:mm" value-format="HH:mm" style="width:100%" /></el-form-item>
        <el-form-item label="最大接诊数"><el-input-number v-model="form.maxAppointments" :min="1" :max="20" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api'
import { ElMessage } from 'element-plus'

const weekMap = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五', 6: '周六', 7: '周日' }
const list = ref([])
const dialogVisible = ref(false)
const form = ref({})

const load = async () => {
  const res = await request.get('/doctor/schedules')
  list.value = res.data
}
onMounted(load)

const openDialog = () => {
  form.value = { dayOfWeek: 1, startTime: '09:00', endTime: '17:00', maxAppointments: 5 }
  dialogVisible.value = true
}

const save = async () => {
  await request.post('/doctor/schedules', form.value)
  ElMessage.success('排班添加成功')
  dialogVisible.value = false
  load()
}

const del = async (id) => {
  await request.delete(`/doctor/schedules/${id}`)
  ElMessage.success('已删除')
  load()
}
</script>
