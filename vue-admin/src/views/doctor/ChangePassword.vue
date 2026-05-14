<template>
  <div>
    <h3>修改密码</h3>
    <el-card style="width:500px;margin-top:20px">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="form.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">提交修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const formRef = ref()
const form = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== form.newPassword) callback(new Error('两次密码不一致'))
  else callback()
}

const rules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

const submit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  await ElMessageBox.confirm('确定修改密码？修改后需要重新登录', '二次确认', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' })
  await request.put('/doctor/change-password', { oldPassword: form.oldPassword, newPassword: form.newPassword })
  ElMessage.success('密码修改成功，请重新登录')
  router.push('/login')
}
</script>
