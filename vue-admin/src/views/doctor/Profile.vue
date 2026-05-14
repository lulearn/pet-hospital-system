<template>
  <div>
    <h3>个人中心</h3>
    <el-card style="width:500px;margin-top:20px">
      <el-form :model="form">
        <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
        <el-form-item label="真实姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="擅长领域"><el-input v-model="form.specialty" /></el-form-item>
        <el-form-item label="简介"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存修改</el-button>
          <el-button type="warning" @click="$router.push('/doctor/change-password')">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import request from '../../api'
import { ElMessage } from 'element-plus'

const form = reactive({ username: '', realName: '', phone: '', email: '', specialty: '', description: '' })

onMounted(async () => {
  const res = await request.get('/doctor/profile')
  Object.assign(form, res.data)
})

const save = async () => { await request.put('/doctor/profile', { ...form }); ElMessage.success('修改成功') }
</script>
