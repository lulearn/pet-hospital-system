<template>
  <div class="login-wrapper">
    <div class="login-box" style="width:420px">
      <h2 style="text-align:center">重置密码</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="form.newPassword" type="password" /></el-form-item>
        <el-form-item label="确认密码"><el-input v-model="form.confirmPassword" type="password" /></el-form-item>
        <el-form-item><el-button type="primary" @click="submit" style="width:100%">重置密码</el-button></el-form-item>
        <el-form-item><el-button @click="$router.push('/login')" style="width:100%">返回登录</el-button></el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = reactive({ username: '', phone: '', newPassword: '', confirmPassword: '' })

const submit = async () => {
  if (!form.username || !form.phone || !form.newPassword) { ElMessage.warning('请填写完整信息'); return }
  if (form.newPassword !== form.confirmPassword) { ElMessage.warning('两次输入的密码不一致'); return }
  await request.post('/reset-password', { username: form.username, phone: form.phone, newPassword: form.newPassword })
  ElMessage.success('密码重置成功，请使用新密码登录')
  router.push('/login')
}
</script>

<style scoped>
.login-wrapper { display:flex; justify-content:center; align-items:center; height:100vh; background:#f0f2f5 }
.login-box { background:#fff; padding:40px; border-radius:8px; box-shadow:0 2px 12px rgba(0,0,0,0.1) }
</style>
