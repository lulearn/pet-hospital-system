import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: () => {
      const role = localStorage.getItem('role')
      if (role === 'ADMIN') return '/admin/dashboard'
      if (role === 'DOCTOR') return '/doctor/dashboard'
      if (role === 'USER') return '/user/dashboard'
      return '/login'
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/login/Register.vue')
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('../views/login/ResetPassword.vue')
  },
  // 用户端
  {
    path: '/user',
    component: () => import('../views/user/Layout.vue'),
    meta: { role: 'USER' },
    children: [
      { path: 'dashboard', component: () => import('../views/user/Dashboard.vue') },
      { path: 'doctors', component: () => import('../views/user/Doctors.vue') },
      { path: 'appointments', component: () => import('../views/user/Appointments.vue') },
      { path: 'medicines', component: () => import('../views/user/Medicines.vue') },
      { path: 'orders', component: () => import('../views/user/Orders.vue') },
      { path: 'recharge', component: () => import('../views/user/Recharge.vue') },
      { path: 'consultations', component: () => import('../views/user/Consultations.vue') },
      { path: 'pets', component: () => import('../views/user/Pets.vue') },
      { path: 'notifications', component: () => import('../views/user/Notifications.vue') },
      { path: 'profile', component: () => import('../views/user/Profile.vue') },
      { path: 'change-password', component: () => import('../views/user/ChangePassword.vue') }
    ]
  },
  // 医生端
  {
    path: '/doctor',
    component: () => import('../views/doctor/Layout.vue'),
    meta: { role: 'DOCTOR' },
    children: [
      { path: 'dashboard', component: () => import('../views/doctor/Dashboard.vue') },
      { path: 'appointments', component: () => import('../views/doctor/Appointments.vue') },
      { path: 'consultations', component: () => import('../views/doctor/Consultations.vue') },
      { path: 'schedules', component: () => import('../views/doctor/Schedules.vue') },
      { path: 'profile', component: () => import('../views/doctor/Profile.vue') },
      { path: 'change-password', component: () => import('../views/doctor/ChangePassword.vue') }
    ]
  },
  // 管理员端
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    meta: { role: 'ADMIN' },
    children: [
      { path: 'dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'users', component: () => import('../views/admin/Users.vue') },
      { path: 'doctors', component: () => import('../views/admin/Doctors.vue') },
      { path: 'medicines', component: () => import('../views/admin/Medicines.vue') },
      { path: 'appointments', component: () => import('../views/admin/Appointments.vue') },
      { path: 'orders', component: () => import('../views/admin/Orders.vue') },
      { path: 'consultations', component: () => import('../views/admin/Consultations.vue') },
      { path: 'profile', component: () => import('../views/admin/Profile.vue') },
      { path: 'change-password', component: () => import('../views/admin/ChangePassword.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  if (to.path !== '/login' && to.path !== '/register' && to.path !== '/reset-password' && !token) {
    next('/login')
  } else if (to.meta.role && to.meta.role !== role) {
    next('/login')
  } else {
    next()
  }
})

export default router
