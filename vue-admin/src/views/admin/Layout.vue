<template>
  <el-container style="min-height:100vh">
    <el-aside width="220px" style="background:#304156">
      <div class="logo">🐾 宠物医院-管理端</div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/admin/dashboard"><el-icon><HomeFilled /></el-icon> 首页</el-menu-item>
        <el-menu-item index="/admin/users"><el-icon><UserFilled /></el-icon> 用户管理</el-menu-item>
        <el-menu-item index="/admin/doctors"><el-icon><User /></el-icon> 医生管理</el-menu-item>
        <el-menu-item index="/admin/appointments"><el-icon><Calendar /></el-icon> 预约记录</el-menu-item>
        <el-menu-item index="/admin/medicines"><el-icon><FirstAidKit /></el-icon> 药品管理</el-menu-item>
        <el-menu-item index="/admin/orders"><el-icon><Document /></el-icon> 订单管理</el-menu-item>
        <el-menu-item index="/admin/consultations"><el-icon><Edit /></el-icon> 就诊记录</el-menu-item>
        <el-menu-item index="/admin/profile"><el-icon><Avatar /></el-icon> 个人中心</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex;align-items:center;justify-content:flex-end;background:#fff;border-bottom:1px solid #e6e6e6">
        <span>管理员，欢迎您！</span>
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
const logout = () => { userStore.logout(); router.push('/login') }
</script>

<style scoped>
.logo { color: #fff; text-align: center; padding: 20px 0; font-size: 16px; font-weight: bold; border-bottom: 1px solid #1f2d3d; }
</style>
