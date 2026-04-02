import { defineStore } from 'pinia'
import { get } from '@/utils/request'
import { ElMessage } from 'element-plus'
import type { UserInfo } from '@/types'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('userToken') || '',
    userInfo: null as UserInfo | null,
    isLoginModalVisible: false
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    getUserInfo: (state) => state.userInfo,
    getUsername: (state) => state.userInfo?.nickname || '',
    getUserAvatar: (state) => state.userInfo?.avatar || ''
  },

  actions: {
    setToken(token: string) {
      this.token = token
      localStorage.setItem('userToken', token)
    },

    setUserInfo(userInfo: UserInfo) {
      this.userInfo = userInfo
    },

    async fetchUserInfo() {
      if (!this.token) return

      try {
        const response = await get('/user/info/detail')
        if (response.code === 1) {
          this.setUserInfo(response.data)
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    },

    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('userToken')
      // 关闭登录弹窗
      this.isLoginModalVisible = false
      ElMessage.success('已退出登录')
    },

    showLoginModal() {
      this.isLoginModalVisible = true
    },

    ensureLoginModal() {
      this.isLoginModalVisible = true
    },

    hideLoginModal() {
      this.isLoginModalVisible = false
    },

    checkAuth() {
      return this.token
    }
  }
})