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
            <!-- 用户头像或登录按钮 -->
            <template v-if="authStore.isLoggedIn">
              <div class="relative">
                <!-- 头像 -->
                <div
                  class="w-8 h-8 bg-gray-300 rounded-full cursor-pointer overflow-hidden hover:opacity-80 transition-opacity relative"
                  @click="handleAvatarClick"
                >
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

                <!-- 下拉菜单 -->
                <div
                  v-if="showUserMenu"
                  class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-2 z-50"
                  @click.stop
                >
                  <div class="px-4 py-2 border-b border-gray-100">
                    <p class="text-sm font-medium text-gray-900">{{ authStore.getUsername }}</p>
                    <p class="text-xs text-gray-500">点击头像查看主页</p>
                  </div>
                  <router-link
                    to="/user/profile"
                    class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                    @click="showUserMenu = false"
                  >
                    我的主页
                  </router-link>
                  <button
                    @click="handleLogout"
                    class="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
                  >
                    退出登录
                  </button>
                </div>
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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AuthModal from '@/components/AuthModal.vue'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const router = useRouter()
const showUserMenu = ref(false)

// 处理头像点击
const handleAvatarClick = (event: Event) => {
  event.stopPropagation()
  // 如果菜单已经打开，则跳转到个人主页
  if (showUserMenu.value) {
    router.push('/user/profile')
    showUserMenu.value = false
  } else {
    // 否则打开菜单
    showUserMenu.value = true
  }
}

// 处理退出登录
const handleLogout = () => {
  authStore.logout()
  showUserMenu.value = false
  router.push('/')
  ElMessage.success('已退出登录')
}

// 点击其他地方关闭下拉菜单
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  const dropdown = document.querySelector('.absolute.right-0.mt-2.w-48')
  if (dropdown && !dropdown.contains(target)) {
    showUserMenu.value = false
  }
}

// 监听点击事件
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>