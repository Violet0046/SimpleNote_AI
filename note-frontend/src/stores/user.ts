import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('userToken') || '',
    isLoggedIn: !!localStorage.getItem('userToken'),
    userInfo: null as any
  }),

  actions: {
    setToken(token: string) {
      this.token = token
      this.isLoggedIn = true
      localStorage.setItem('userToken', token)
    },

    logout() {
      this.token = ''
      this.isLoggedIn = false
      this.userInfo = null
      localStorage.removeItem('userToken')
    },

    checkAuth() {
      return this.token
    }
  }
})