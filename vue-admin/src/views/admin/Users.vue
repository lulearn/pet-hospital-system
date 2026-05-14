<template>
  <div>
    <h3>用户管理</h3>
    <el-button type="primary" @click="openDialog()" style="margin-bottom:15px">添加用户</el-button>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="真实姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="role" label="角色" width="80" />
      <el-table-column prop="balance" label="余额" width="100" />
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑用户':'添加用户'" width="500px">
      <el-form :model="form">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" :placeholder="isEdit?'留空不修改':''" /></el-form-item>
        <el-form-item label="真实姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option value="USER" label="用户" />
            <el-option value="ADMIN" label="管理员" />
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
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})

const load = async () => { const res = await request.get('/admin/users'); list.value = res.data }
onMounted(load)

const openDialog = (row) => {
  isEdit.value = !!row?.id
  form.value = row?.id ? { ...row, password: '' } : { username: '', password: '', realName: '', phone: '', email: '', role: 'USER' }
  dialogVisible.value = true
}

const save = async () => {
  if (isEdit.value) {
    const data = { ...form.value }
    if (!data.password) delete data.password
    await request.put('/admin/users', data)
  } else {
    await request.post('/admin/users', form.value)
  }
  ElMessage.success('保存成功'); dialogVisible.value = false; load()
}

const del = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await request.delete(`/admin/users/${id}`)
  ElMessage.success('删除成功'); load()
}
</script>
