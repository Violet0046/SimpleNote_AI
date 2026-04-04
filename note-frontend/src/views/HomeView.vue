<template>
  <div class="flex h-screen overflow-hidden bg-white">
    <div class="mx-auto flex w-full max-w-[2130px] px-[30px] 2xl:px-[210px] h-full">
      
      <aside class="w-[320px] flex-shrink-0 h-full flex flex-col bg-white">
        <div class="flex items-center pl-[50px] mt-[25px]">
          <svg class="w-8 h-8 text-[#FF2442]" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8-8-3.59 8-8 8zm-1-13h2v6h-2zm0 8h2v2h-2z"/>
          </svg>
          <span class="ml-2 text-lg font-medium text-[#FF2442]">小红书</span>
        </div>

        <div class="flex-1 flex flex-col mt-[45px]">
          <nav class="flex flex-col space-y-[8px]">
            <router-link
              to="/"
              @click="handleDiscoverClick"
              class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-medium transition-colors"
              :class="$route.path === '/' ? 'text-[#FF2442] bg-[#F7F7F7]' : 'text-gray-800'"
            >
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <span>发现</span>
            </router-link>

            <router-link to="/live" class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-medium text-gray-800 transition-colors">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z" /></svg>
              <span>直播</span>
            </router-link>
            
            <router-link to="/publish" class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-medium text-gray-800 transition-colors">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" /></svg>
              <span>发布</span>
            </router-link>
            
            <router-link to="/notifications" class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-medium text-gray-800 transition-colors">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" /></svg>
              <span>通知</span>
            </router-link>

            <template v-if="authStore.isLoggedIn">
              <router-link
                to="/profile"
                class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-medium transition-colors"
                :class="$route.path === '/profile' ? 'text-[#FF2442] bg-[#F7F7F7]' : 'text-gray-800'"
              >
                <div class="w-8 h-8 rounded-full overflow-hidden flex-shrink-0 bg-gray-100 border border-gray-200 shadow-sm">
                  <img v-if="authStore.userInfo?.avatar" :src="authStore.userInfo.avatar" alt="avatar" class="w-full h-full object-cover" v-image-error="{ type: 'avatar' }" />
                  <span v-else class="w-full h-full flex items-center justify-center text-gray-500 text-[12px] font-bold">{{ authStore.userInfo?.nickname?.charAt(0).toUpperCase() || 'U' }}</span>
                </div>
                <span>我</span>
              </router-link>
            </template>
            <template v-else>
              <button @click="authStore.showLoginModal()" class="flex items-center justify-center h-[56px] w-[272px] mx-auto rounded-full bg-[#FF2E4D] hover:bg-[#E02844] text-white text-lg font-medium transition-colors mt-2">
                登录
              </button>
            </template>
          </nav>

          <div class="mt-auto mb-[30px] relative" id="more-menu-container">
            <transition name="fade">
              <div v-if="showMoreMenu" class="absolute bottom-[65px] left-[24px] w-[240px] bg-white rounded-2xl shadow-[0_4px_20px_rgba(0,0,0,0.15)] py-2 z-50 border border-gray-100">
                <button @click="toggleDarkMode" class="w-full flex items-center px-5 py-3 hover:bg-gray-50 transition-colors text-gray-800">
                  <svg v-if="!isDarkMode" class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" /></svg>
                  <svg v-else class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" /></svg>
                  <span class="ml-3 text-[15px] font-medium">模式</span>
                </button>
                <button v-if="authStore.isLoggedIn" @click="handleLogout" class="w-full flex items-center px-5 py-3 hover:bg-gray-50 transition-colors text-gray-800 border-t border-gray-100">
                  <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
                  <span class="ml-3 text-[15px] font-medium">退出登录</span>
                </button>
              </div>
            </transition>
            <button @click.stop="showMoreMenu = !showMoreMenu" class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-medium text-gray-800 transition-colors">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" /></svg>
              <span>更多</span>
            </button>
          </div>
        </div>
      </aside>

      <router-view v-slot="{ Component, route }">
        <keep-alive>
          <component :is="Component" v-if="route.path === '/'" :key="'feed'" />
        </keep-alive>
        <component :is="Component" v-if="route.path !== '/'" :key="route.path" />
      </router-view>
      
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, provide } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
const authStore = useAuthStore()
const route = useRoute()

// 建立通道：把刷新信号提供给 FeedView
const refreshDiscoverTrigger = ref(0)
provide('refreshDiscoverTrigger', refreshDiscoverTrigger)

// 处理点击发现按钮
const handleDiscoverClick = (e: MouseEvent) => {
  if (route.path === '/') {
    // 如果当前【已经在】发现页，阻止原本的跳转行为
    e.preventDefault()
    // 数字+1，触发子组件 FeedView 的监听器，执行下拉刷新动画
    refreshDiscoverTrigger.value++ 
  }
  // 如果不在发现页 (比如在 /profile)，什么都不做，让它自然地 router 跳转回 '/'，
  // 并且因为有 keep-alive，它会瞬间恢复成之前看过的样子！
}
// 菜单与深浅模式状态
const showMoreMenu = ref(false)
const isDarkMode = ref(false)
const router = useRouter()
// 退出登录
const handleLogout = () => {
  showMoreMenu.value = false
  ElMessage.success('正在安全退出...')
  
  setTimeout(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('auth') 
    if (typeof authStore.logout === 'function') {
      authStore.logout()
    }
    
    router.push('/')
    
    // 延迟一点点刷新，确保路由跳转的痕迹生效
    setTimeout(() => {
      window.location.reload()
    }, 100)
    
  }, 500)
}

// 切换深浅模式
const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value
  showMoreMenu.value = false
  if (isDarkMode.value) {
    document.documentElement.classList.add('dark-theme')
  } else {
    document.documentElement.classList.remove('dark-theme')
  }
}

// 点击空白关闭菜单
const closeMenuOnClickOutside = (e: MouseEvent) => {
  const container = document.getElementById('more-menu-container')
  if (container && !container.contains(e.target as Node)) {
    showMoreMenu.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', closeMenuOnClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', closeMenuOnClickOutside)
})
</script>

<style>
/* 这里保留你之前写的所有的全局 CSS (滚动条隐藏、动画、黑夜模式) */
/* 为了篇幅不再重复粘贴，请把刚才代码里的 <style> 完整复制回这里 */
</style>

<style>
/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 瀑布流项目间距优化 */
.break-inside-avoid {
  break-inside: avoid;
  margin-bottom: 1rem;
}

/* 图片加载动画 */
.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: .5;
  }
}

/* 悬浮菜单过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* 🌙 深色模式全局强制覆盖 (魔法代码) */
html.dark-theme,
html.dark-theme body,
html.dark-theme #app,
html.dark-theme .bg-white,
html.dark-theme main,
html.dark-theme aside {
  background-color: #0A0A0A !important;
  color: #FFFFFF !important;
}

/* 覆盖文本颜色 */
html.dark-theme .text-gray-800,
html.dark-theme .text-gray-900,
html.dark-theme .text-gray-700,
html.dark-theme span {
  color: #FFFFFF !important;
}

/* 覆盖浅灰色背景(如搜索框、导航项)变成深灰 */
html.dark-theme .bg-\[\#F7F7F7\],
html.dark-theme .hover\:bg-\[\#F7F7F7\]:hover,
html.dark-theme .bg-gray-100,
html.dark-theme .bg-gray-50 {
  background-color: #1A1A1A !important;
}

/* 覆盖输入框内文字和占位符 */
html.dark-theme input {
  color: #FFFFFF !important;
}
html.dark-theme input::placeholder {
  color: #666666 !important;
}

/* 覆盖弹出菜单的边框 */
html.dark-theme .border-gray-100 {
  border-color: #333333 !important;
}
</style>