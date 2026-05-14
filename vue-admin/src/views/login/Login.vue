<template>
  <div class="login-container">
    <div class="login-card">
      <h2>宠物医院后台管理系统</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="role">
          <el-radio-group v-model="form.role">
            <el-radio value="USER">用户</el-radio>
            <el-radio value="DOCTOR">医生</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" :loading="loading" style="width:100%">登 录</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align:center;display:flex;justify-content:space-between">
        <el-link type="primary" @click="$router.push('/reset-password')">忘记密码</el-link>
        <el-link type="primary" @click="$router.push('/register')">没有账号？立即注册</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store'
import request from '../../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '', role: 'USER' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const login = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const role = form.username === 'admin' ? 'ADMIN' : form.role
    const res = await request.post('/login', { username: form.username, password: form.password, role })
    userStore.setLogin(res.data)
    ElMessage.success('登录成功')
    if (res.data.role === 'ADMIN') router.push('/admin/dashboard')
    else if (res.data.role === 'DOCTOR') router.push('/doctor/dashboard')
    else router.push('/user/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}
.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>
