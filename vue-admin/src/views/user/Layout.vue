<template>
  <el-container style="min-height:100vh">
    <el-aside width="220px" style="background:#304156">
      <div class="logo">🐾 宠物医院-用户端</div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/user/dashboard"><el-icon><HomeFilled /></el-icon> 首页</el-menu-item>
        <el-menu-item index="/user/doctors"><el-icon><UserFilled /></el-icon> 就诊预约</el-menu-item>
        <el-menu-item index="/user/appointments"><el-icon><Calendar /></el-icon> 预约管理</el-menu-item>
        <el-menu-item index="/user/medicines"><el-icon><FirstAidKit /></el-icon> 药品管理</el-menu-item>
        <el-menu-item index="/user/orders"><el-icon><Document /></el-icon> 订单管理</el-menu-item>
        <el-menu-item index="/user/consultations"><el-icon><Edit /></el-icon> 就诊记录</el-menu-item>
        <el-menu-item index="/user/pets"><el-icon><House /></el-icon> 宠物档案</el-menu-item>
        <el-menu-item index="/user/recharge"><el-icon><Wallet /></el-icon> 充值中心</el-menu-item>
        <el-menu-item index="/user/notifications"><el-icon><Bell /></el-icon> 消息通知</el-menu-item>
        <el-menu-item index="/user/profile"><el-icon><User /></el-icon> 个人中心</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex;align-items:center;justify-content:flex-end;background:#fff;border-bottom:1px solid #e6e6e6">
        <span>{{ user?.realName }}，欢迎您！</span>
        <el-button type="danger" text @click="logout" style="margin-left:20px">退出登录</el-button>
      </el-header>
      <el-main style="background:#f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store'

const router = useRouter()
const userStore = useUserStore()
const user = userStore.user

const logout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.logo { color: #fff; text-align: center; padding: 20px 0; font-size: 16px; font-weight: bold; border-bottom: 1px solid #1f2d3d; }
</style>
