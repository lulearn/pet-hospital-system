<template>
  <div>
    <h3>宠物档案</h3>
    <el-button type="primary" @click="openDialog()" style="margin-bottom:15px">添加宠物</el-button>
    <el-table :data="list" border stripe>
      <el-table-column prop="name" label="宠物名" />
      <el-table-column prop="species" label="种类" />
      <el-table-column prop="breed" label="品种" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑宠物':'添加宠物'" width="500px">
      <el-form :model="form">
        <el-form-item label="宠物名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="种类"><el-input v-model="form.species" placeholder="如:狗、猫、兔" /></el-form-item>
        <el-form-item label="品种"><el-input v-model="form.breed" placeholder="如:金毛、英短" /></el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" :max="30" /></el-form-item>
        <el-form-item label="性别"><el-select v-model="form.gender"><el-option value="公" label="公" /><el-option value="母" label="母" /></el-select></el-form-item>
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
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const load = async () => { const res = await request.get('/pets'); list.value = res.data }
onMounted(load)

const openDialog = (row) => {
  isEdit.value = !!row?.id
  form.value = row?.id ? { ...row } : { name: '', species: '', breed: '', age: 0, gender: '' }
  dialogVisible.value = true
}

const save = async () => {
  if (isEdit.value) await request.put('/pets', form.value)
  else await request.post('/pets', form.value)
  ElMessage.success('保存成功'); dialogVisible.value = false; load()
}

const del = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await request.delete(`/pets/${id}`)
  ElMessage.success('删除成功'); load()
}
</script>
