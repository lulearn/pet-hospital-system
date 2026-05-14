<template>
  <div>
    <h3>就诊记录</h3>
    <el-table :data="list" border stripe>
      <el-table-column label="医生" width="100">
        <template #default="{row}">{{ doctorMap[row.doctorId] || row.doctorId }}</template>
      </el-table-column>
      <el-table-column prop="petName" label="宠物名" width="100" />
      <el-table-column prop="diagnosis" label="诊断结果" />
      <el-table-column prop="prescription" label="处方" />
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="row.status==='COMPLETED'?'success':'warning'">{{ row.status==='COMPLETED'?'已完成':'就诊中' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="评分" width="150">
        <template #default="{row}">
          <el-rate v-if="row.rating" v-model="row.rating" disabled size="small" />
          <span v-else-if="row.status!=='COMPLETED'">-</span>
          <span v-else style="color:#909399">待评价</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="就诊时间" width="160" />
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button size="small" @click="showMedicines(row)">药方</el-button>
          <el-button v-if="row.status==='COMPLETED' && !row.rating" size="small" type="warning" @click="rateRow=row;rateDialog=true">评价</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 药方弹窗 -->
    <el-dialog v-model="medDialog" title="药方明细" width="600px">
      <el-table :data="medicines" border>
        <el-table-column label="药品名">
          <template #default="{row}">{{ medMap[row.medicineId] }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="dosage" label="用法用量" />
        <el-table-column label="操作" width="100">
          <template #default="{row}">
            <el-button size="small" type="primary" @click="buyMedicine(row)">购买</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog v-model="rateDialog" title="评价就诊" width="400px">
      <div style="text-align:center">
        <el-rate v-model="rateValue" size="large" />
      </div>
      <template #footer>
        <el-button @click="rateDialog=false">取消</el-button>
        <el-button type="primary" @click="submitRate">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api'
import { ElMessage } from 'element-plus'

const list = ref([])
const doctorMap = ref({})
const medMap = ref({})
const medicines = ref([])
const medDialog = ref(false)
const rateDialog = ref(false)
const rateRow = ref(null)
const rateValue = ref(5)

onMounted(async () => {
  const [consRes, docRes, medRes] = await Promise.all([
    request.get('/consultations'),
    request.get('/doctors'),
    request.get('/medicines')
  ])
  list.value = consRes.data
  docRes.data.forEach(d => { doctorMap.value[d.id] = d.realName })
  medRes.data.forEach(m => { medMap.value[m.id] = m.name })
})

const showMedicines = async (row) => {
  const res = await request.get(`/consultations/${row.id}/medicines`)
  medicines.value = res.data
  medDialog.value = true
}

const buyMedicine = async (pm) => {
  await request.post(`/consultations/${pm.consultationId}/buy-medicine`, {
    medicineId: pm.medicineId, quantity: pm.quantity
  })
  ElMessage.success('已下单，请前往订单管理支付')
  medDialog.value = false
}

const submitRate = async () => {
  if (!rateRow.value) return
  await request.put(`/consultations/${rateRow.value.id}/rate`, { rating: rateValue.value })
  rateRow.value.rating = rateValue.value
  ElMessage.success('评价成功')
  rateDialog.value = false
}
</script>
