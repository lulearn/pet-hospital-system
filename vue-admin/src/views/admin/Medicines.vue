<template>
  <div>
    <h3>药品管理</h3>
    <el-button type="primary" @click="openDialog()" style="margin-bottom:15px">添加药品</el-button>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="药品名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="price" label="单价(元)" width="100" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑药品':'添加药品'" width="500px">
      <el-form :model="form">
        <el-form-item label="药品名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
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
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const load = async () => { const res = await request.get('/admin/medicines'); list.value = res.data }
onMounted(load)

const openDialog = (row) => {
  isEdit.value = !!row?.id
  form.value = row?.id ? { ...row } : { name: '', description: '', price: 0, stock: 0 }
  dialogVisible.value = true
}

const save = async () => {
  if (isEdit.value) await request.put('/admin/medicines', form.value)
  else await request.post('/admin/medicines', form.value)
  ElMessage.success('保存成功'); dialogVisible.value = false; load()
}

const del = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await request.delete(`/admin/medicines/${id}`)
  ElMessage.success('删除成功'); load()
}
</script>
