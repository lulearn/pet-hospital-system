<template>
  <div>
    <h3>充值中心</h3>
    <el-card style="width:500px;margin-top:20px">
      <el-form :model="form">
        <el-form-item label="当前余额"><h2>¥{{ profile.balance || 0 }}</h2></el-form-item>
        <el-form-item label="充值金额">
          <el-input-number v-model="form.amount" :min="1" :max="99999" :step="100" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="recharge">确认充值</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import request from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const profile = ref({})
const form = reactive({ amount: 100 })

onMounted(async () => { const res = await request.get('/user/profile'); profile.value = res.data })

const recharge = async () => {
  await ElMessageBox.confirm(`确认充值 ¥${form.amount}？`, '二次确认', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' })
  await request.post('/recharge', { amount: form.amount })
  ElMessage.success('充值成功')
  const res = await request.get('/user/profile'); profile.value = res.data
}
</script>
