import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    role: localStorage.getItem('role') || ''
  }),
  actions: {
    setLogin(data) {
      this.token = data.token
      this.user = data.user
      this.role = data.role
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
      localStorage.setItem('role', data.role)
    },
    logout() {
      this.token = ''
      this.user = null
      this.role = ''
      localStorage.clear()
    }
  }
})
