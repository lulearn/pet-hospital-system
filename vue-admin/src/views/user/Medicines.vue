<template>
  <div>
    <h3>药品管理</h3>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="药品名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="price" label="单价(元)" width="100" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column label="操作" width="120">
        <template #default="{row}">
          <el-button size="small" type="primary" @click="buy(row)">订购</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="订购药品" width="400px">
      <el-form :model="form">
        <el-form-item label="药品">{{ selectedMedicine?.name }}</el-form-item>
        <el-form-item label="单价">{{ selectedMedicine?.price }} 元</el-form-item>
        <el-form-item label="数量"><el-input-number v-model="form.quantity" :min="1" :max="99" /></el-form-item>
        <el-form-item label="总价">{{ (selectedMedicine?.price || 0) * form.quantity }} 元</el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确认订购</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api'
import { ElMessage } from 'element-plus'

const list = ref([])
const dialogVisible = ref(false)
const selectedMedicine = ref(null)
const form = ref({ quantity: 1 })

const loadMedicines = async () => { const res = await request.get('/medicines'); list.value = res.data }
onMounted(loadMedicines)

const buy = (row) => {
  selectedMedicine.value = row
  form.value = { medicineId: row.id, quantity: 1 }
  dialogVisible.value = true
}

const submit = async () => {
  await request.post('/orders', { medicineId: form.value.medicineId, quantity: form.value.quantity })
  ElMessage.success('订购成功')
  dialogVisible.value = false
  loadMedicines()
}
</script>
