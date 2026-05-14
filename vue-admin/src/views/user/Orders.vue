<template>
  <div>
    <h3>订单管理</h3>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="药品名称" width="120">
        <template #default="{row}">{{ medicineMap[row.medicineId] || row.medicineId }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="totalPrice" label="金额(元)" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="row.status==='PAID'?'primary':row.status==='SHIPPED'?'warning':row.status==='RECEIVED'?'success':row.status==='UNPAID'?'info':'danger'">
            {{ {UNPAID:'未支付',PAID:'已支付',SHIPPED:'已发货',RECEIVED:'已收货',COMPLETED:'已完成',CANCELLED:'已取消'}[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="{row}">
          <el-button v-if="row.status==='UNPAID'" size="small" type="success" @click="pay(row.id)">支付</el-button>
          <el-button v-if="row.status==='SHIPPED'" size="small" type="primary" @click="receive(row.id)">确认收货</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
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

const loadOrders = async () => { const res = await request.get('/orders'); list.value = res.data }
onMounted(async () => {
  const [ordersRes, medRes] = await Promise.all([request.get('/orders'), request.get('/medicines')])
  list.value = ordersRes.data
  medRes.data.forEach(m => { medicineMap.value[m.id] = m.name })
})

const pay = async (id) => {
  await ElMessageBox.confirm('确认支付？', '提示', { type: 'warning' })
  await request.post(`/orders/${id}/pay`)
  ElMessage.success('支付成功')
  loadOrders()
}

const receive = async (id) => {
  await request.post(`/orders/${id}/receive`)
  ElMessage.success('已确认收货')
  loadOrders()
}

const del = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await request.delete(`/orders/${id}`)
  ElMessage.success('删除成功')
  loadOrders()
}
</script>
