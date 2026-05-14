<template>
  <div>
    <h3>订单管理</h3>
    <el-table :data="list" border stripe>
      <el-table-column label="用户" width="100">
        <template #default="{row}">{{ userMap[row.userId] || row.userId }}</template>
      </el-table-column>
      <el-table-column label="药品名称" width="120">
        <template #default="{row}">{{ medicineMap[row.medicineId] || row.medicineId }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="totalPrice" label="金额(元)" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="row.status==='PAID'?'primary':row.status==='SHIPPED'?'warning':row.status==='RECEIVED'?'success':row.status==='UNPAID'?'info':'danger'">
            {{ {UNPAID:'未支付',PAID:'已支付',SHIPPED:'已发货',RECEIVED:'已收货',COMPLETED:'已完成'}[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="时间" width="160">
        <template #default="{row}">{{ row.createTime }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button v-if="row.status==='PAID'" size="small" type="warning" @click="ship(row.id)">发货</el-button>
          <el-button v-if="row.status==='UNPAID'||row.status==='RECEIVED'" size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const medicineMap = ref({})
const userMap = ref({})

const load = async () => { const res = await request.get('/admin/orders'); list.value = res.data }
onMounted(async () => {
  const [ordersRes, medRes, userRes] = await Promise.all([
    request.get('/admin/orders'),
    request.get('/admin/medicines'),
    request.get('/admin/users')
  ])
  list.value = ordersRes.data
  medRes.data.forEach(m => { medicineMap.value[m.id] = m.name })
  userRes.data.forEach(u => { userMap.value[u.id] = u.realName })
})

const ship = async (id) => {
  await request.put(`/admin/orders/${id}/ship`)
  ElMessage.success('发货成功'); load()
}

const del = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await request.delete(`/admin/orders/${id}`)
  ElMessage.success('删除成功'); load()
}
</script>
