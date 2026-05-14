<template>
  <div>
    <h3>预约管理</h3>
    <el-button type="primary" @click="openDialog()" style="margin-bottom:15px">添加预约</el-button>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
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
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑预约':'添加预约'" width="500px">
      <el-form :model="form">
        <el-form-item label="宠物名称"><el-input v-model="form.petName" /></el-form-item>
        <el-form-item label="宠物种类"><el-input v-model="form.species" /></el-form-item>
        <el-form-item label="预约时间"><el-date-picker v-model="form.appointmentTime" type="datetime" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="选择医生">
          <el-select v-model="form.doctorId" placeholder="请选择医生" style="width:100%">
            <el-option v-for="d in doctors" :key="d.id" :label="`${d.realName} - ${d.specialty}`" :value="d.id" />
          </el-select>
        </el-form-item>
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
const doctors = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const load = async () => {
  const [apptRes, docRes] = await Promise.all([
    request.get('/appointments'),
    request.get('/doctors')
  ])
  list.value = apptRes.data
  doctors.value = docRes.data
}
onMounted(load)

const openDialog = (row) => {
  isEdit.value = !!row?.id
  form.value = row?.id ? { ...row } : { doctorId: 1, petName: '', species: '', description: '', appointmentTime: '' }
  dialogVisible.value = true
}

const save = async () => {
  if (isEdit.value) {
    await request.put('/appointments', form.value)
  } else {
    await request.post('/appointments', form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const del = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await request.delete(`/appointments/${id}`)
  ElMessage.success('删除成功')
  load()
}
</script>
