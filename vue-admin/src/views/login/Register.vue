<template>
  <div class="login-container">
    <div class="login-card">
      <h2>用户注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-form-item prop="realName">
          <el-input v-model="form.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱" />
        </el-form-item>
        <el-form-item v-if="form.role === 'DOCTOR'" prop="specialty">
          <el-input v-model="form.specialty" placeholder="擅长领域" />
        </el-form-item>
        <el-form-item prop="role">
          <el-radio-group v-model="form.role">
            <el-radio value="USER">用户</el-radio>
            <el-radio value="DOCTOR">医生</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="register" :loading="loading" style="width:100%">注 册</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align:center">
        <el-link type="primary" @click="$router.push('/login')">已有账号？返回登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', realName: '', phone: '', email: '', specialty: '', role: 'USER' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

const register = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await request.post('/register', { ...form })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
  width: 420px;
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
