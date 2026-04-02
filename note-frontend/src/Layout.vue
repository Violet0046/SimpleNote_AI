<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部 Navbar -->
    <nav class="fixed top-0 left-0 right-0 z-50 bg-white shadow-sm border-b border-gray-100">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <!-- Logo -->
          <div class="flex-shrink-0">
            <router-link to="/" class="flex items-center">
              <div class="w-8 h-8 bg-red-500 rounded-full flex items-center justify-center">
                <span class="text-white font-bold text-lg">X</span>
              </div>
              <span class="ml-2 text-xl font-semibold text-gray-900">小红书</span>
            </router-link>
          </div>

          <!-- 中间搜索框 -->
          <div class="flex-1 max-w-lg mx-8">
            <div class="relative">
              <input
                type="text"
                placeholder="搜索你感兴趣的内容"
                class="w-full px-4 py-2 pl-10 pr-4 text-gray-700 bg-gray-100 border border-gray-200 rounded-full focus:outline-none focus:border-red-500 focus:bg-white transition-colors"
              >
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
              </div>
            </div>
          </div>

          <!-- 右侧按钮 -->
          <div class="flex items-center space-x-4">
            <!-- 发帖按钮 -->
            <router-link
              to="/create"
              class="px-4 py-2 text-sm font-medium text-white bg-red-500 rounded-full hover:bg-red-600 transition-colors"
            >
              发布
            </router-link>

            <!-- 用户头像或登录按钮 -->
            <template v-if="authStore.isLoggedIn">
              <div class="w-8 h-8 bg-gray-300 rounded-full cursor-pointer overflow-hidden">
                <img
                  v-if="authStore.getUserAvatar"
                  :src="authStore.getUserAvatar"
                  alt="用户头像"
                  class="w-full h-full object-cover"
                >
                <span v-else class="w-full h-full flex items-center justify-center text-gray-600 text-sm">
                  {{ authStore.getUsername?.charAt(0).toUpperCase() || 'U' }}
                </span>
              </div>
            </template>
            <template v-else>
              <button
                @click="authStore.showLoginModal()"
                class="px-4 py-2 text-sm font-medium text-red-500 border border-red-500 rounded-full hover:bg-red-50 transition-colors"
              >
                登录
              </button>
            </template>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主内容区域 -->
    <main class="pt-16">
      <router-view></router-view>
    </main>

    <!-- 登录/注册弹窗 -->
    <AuthModal
      v-if="authStore.isLoginModalVisible"
      @close="authStore.hideLoginModal"
    />
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import AuthModal from '@/components/AuthModal.vue'

const authStore = useAuthStore()
</script>