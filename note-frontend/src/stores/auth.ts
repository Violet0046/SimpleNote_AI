import { defineStore } from 'pinia'
import { ElMessage } from 'element-plus'

import type { UserInfo } from '@/types'
import { get } from '@/utils/request'
import { tokenStorage } from '@/shared/utils/storage'
import { useLikeStore } from './like'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: tokenStorage.get(),
    userInfo: null as UserInfo | null,
    isLoginModalVisible: false,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    getUserInfo: (state) => state.userInfo,
    getUsername: (state) => state.userInfo?.nickname || '',
    getUserAvatar: (state) => state.userInfo?.avatar || '',
  },

  actions: {
    setToken(token: string) {
      this.token = token
      tokenStorage.set(token)
    },

    setUserInfo(userInfo: UserInfo) {
      this.userInfo = userInfo
    },

    async fetchUserInfo() {
      if (!this.token) return

      try {
        const response = await get<UserInfo>('/user/me')
        if (response.code === 1) {
          this.setUserInfo(response.data)
        }
      } catch (error) {
        console.error('Failed to fetch user info:', error)
      }
    },

    logout() {
      this.token = ''
      this.userInfo = null
      tokenStorage.clear()
      this.isLoginModalVisible = false
      const likeStore = useLikeStore()
      likeStore.clear()
      ElMessage.success('Logged out successfully')
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
      return this.token || tokenStorage.get()
    },
  },
})
