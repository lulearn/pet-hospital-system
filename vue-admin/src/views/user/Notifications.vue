<template>
  <div>
    <h3>消息通知</h3>
    <el-button type="primary" size="small" @click="readAll" :disabled="!list.length" style="margin-bottom:15px">全部已读</el-button>
    <el-table :data="list" border stripe>
      <el-table-column prop="title" label="标题" width="150" />
      <el-table-column prop="content" label="内容" />
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="row.isRead?'info':'danger'">{{ row.isRead?'已读':'未读' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="160" />
      <el-table-column label="操作" width="100">
        <template #default="{row}">
          <el-button v-if="!row.isRead" size="small" @click="markRead(row.id)">标记已读</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api'
import { ElMessage } from 'element-plus'

const list = ref([])

const load = async () => {
  const res = await request.get('/notifications')
  list.value = res.data
}
onMounted(load)

const markRead = async (id) => {
  await request.put(`/notifications/${id}/read`)
  load()
}

const readAll = async () => {
  await request.put('/notifications/read-all')
  ElMessage.success('已全部标为已读')
  load()
}
</script>
