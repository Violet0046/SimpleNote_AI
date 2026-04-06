<template>
  <div class="flex h-screen overflow-hidden bg-white">
    <div class="mx-auto flex h-full w-full max-w-[2130px] px-[30px] 2xl:px-[210px]">
      <aside class="w-[320px] h-full flex-shrink-0 flex flex-col bg-white">
        <div class="mt-[25px] flex items-center pl-[50px]">
          <svg class="w-8 h-8 text-[#FF2442]" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-1-13h2v6h-2zm0 8h2v2h-2z"/>
          </svg>
          <span class="ml-2 text-lg font-semibold text-[#FF2442]">简单笔记</span>
        </div>

        <div class="mt-[45px] flex flex-1 flex-col">
          <nav class="flex flex-col space-y-[8px]">
            <RouterLink
              to="/"
              class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-semibold transition-colors"
              :class="route.path === '/' ? 'text-[#FF2442] bg-[#F7F7F7]' : 'text-gray-800'"
              @click="handleDiscoverClick"
            >
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                />
              </svg>
              <span>发现</span>
            </RouterLink>

            <RouterLink
              to="/live"
              class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-semibold transition-colors"
              :class="route.path === '/live' ? 'text-[#FF2442] bg-[#F7F7F7]' : 'text-gray-800'"
            >
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  d="M15 10l4.553-2.276A1 1 0 0121 8.618v6.764a1 1 0 01-1.447.894L15 14M5 18h8a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                />
              </svg>
              <span>直播</span>
            </RouterLink>

            <button
              class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-semibold text-gray-800 transition-colors cursor-pointer"
              type="button"
              @click="openCreatePage"
            >
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  d="M12 4v16m8-8H4"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                />
              </svg>
              <span>发布</span>
            </button>

            <RouterLink
              to="/notifications"
              class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-semibold transition-colors"
              :class="route.path === '/notifications' ? 'text-[#FF2442] bg-[#F7F7F7]' : 'text-gray-800'"
            >
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                />
              </svg>
              <span>通知</span>
            </RouterLink>

            <template v-if="authStore.isLoggedIn">
              <RouterLink
                to="/profile"
                class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-semibold transition-colors"
                :class="route.path === '/profile' ? 'text-[#FF2442] bg-[#F7F7F7]' : 'text-gray-800'"
              >
                <div class="w-8 h-8 rounded-full overflow-hidden flex-shrink-0 bg-gray-100 border border-gray-200 shadow-sm">
                  <img
                    v-if="authStore.userInfo?.avatar"
                    :src="authStore.userInfo.avatar"
                    alt="avatar"
                    class="w-full h-full object-cover"
                    v-image-error="{ type: 'avatar' }"
                  />
                  <span v-else class="w-full h-full flex items-center justify-center text-gray-500 text-[12px] font-bold">
                    {{ authStore.userInfo?.nickname?.charAt(0).toUpperCase() || 'U' }}
                  </span>
                </div>
                <span>我</span>
              </RouterLink>
            </template>

            <template v-else>
              <button
                class="flex items-center justify-center h-[56px] w-[272px] mx-auto rounded-full bg-[#FF2E4D] hover:bg-[#E02844] text-white text-lg font-semibold transition-colors mt-2"
                type="button"
                @click="authStore.showLoginModal()"
              >
                登录
              </button>
            </template>
          </nav>

          <div ref="moreMenuContainerRef" class="mt-auto mb-[30px] relative">
            <Transition name="fade">
              <div
                v-if="showMoreMenu"
                class="absolute bottom-[65px] left-[24px] w-[240px] bg-white rounded-2xl shadow-[0_4px_20px_rgba(0,0,0,0.15)] py-2 z-50 border border-gray-100"
              >
                <button
                  class="w-full flex items-center px-5 py-3 hover:bg-gray-50 transition-colors text-gray-800"
                  type="button"
                  @click="toggleDarkMode"
                >
                  <svg v-if="!isDarkMode" class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                    />
                  </svg>
                  <svg v-else class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                    />
                  </svg>
                  <span class="ml-3 text-[15px] font-medium">模式</span>
                </button>

                <button
                  v-if="authStore.isLoggedIn"
                  class="w-full flex items-center px-5 py-3 hover:bg-gray-50 transition-colors text-gray-800 border-t border-gray-100"
                  type="button"
                  @click="handleLogout"
                >
                  <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                    />
                  </svg>
                  <span class="ml-3 text-[15px] font-medium">退出登录</span>
                </button>
              </div>
            </Transition>

            <button
              class="flex items-center pl-[24px] gap-[16px] h-[56px] w-[272px] mx-auto rounded-full hover:bg-[#F7F7F7] text-lg font-semibold text-gray-800 transition-colors"
              type="button"
              @click.stop="showMoreMenu = !showMoreMenu"
            >
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  d="M4 6h16M4 12h16M4 18h16"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                />
              </svg>
              <span>更多</span>
            </button>
          </div>
        </div>
      </aside>

      <RouterView v-slot="{ Component, route: currentRoute }">
        <KeepAlive>
          <component :is="Component" v-if="currentRoute.path === '/'" :key="'feed'" />
        </KeepAlive>
        <component :is="Component" v-if="currentRoute.path !== '/'" :key="currentRoute.path" />
      </RouterView>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, provide, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { useAuthStore } from '@/stores/auth'
import { removeStorageItem } from '@/shared/utils/storage'

const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()

const refreshDiscoverTrigger = ref(0)
const showMoreMenu = ref(false)
const isDarkMode = ref(false)
const moreMenuContainerRef = ref<HTMLElement | null>(null)

provide('refreshDiscoverTrigger', refreshDiscoverTrigger)

const openCreatePage = () => {
  if (!authStore.isLoggedIn) {
    authStore.showLoginModal()
    return
  }

  const routeUrl = router.resolve('/create')
  window.open(routeUrl.href, '_blank', 'noopener')
}

const handleDiscoverClick = (event: MouseEvent) => {
  if (route.path !== '/') return

  event.preventDefault()
  refreshDiscoverTrigger.value += 1
}

const handleLogout = () => {
  showMoreMenu.value = false
  ElMessage.success('正在安全退出...')

  window.setTimeout(() => {
    removeStorageItem('token')
    removeStorageItem('user')
    removeStorageItem('auth')
    authStore.logout()
    void router.push('/')

    window.setTimeout(() => {
      window.location.reload()
    }, 100)
  }, 500)
}

const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value
  showMoreMenu.value = false

  if (isDarkMode.value) {
    document.documentElement.classList.add('dark-theme')
  } else {
    document.documentElement.classList.remove('dark-theme')
  }
}

const closeMenuOnClickOutside = (event: MouseEvent) => {
  const container = moreMenuContainerRef.value
  if (container && !container.contains(event.target as Node)) {
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
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

html.dark-theme,
html.dark-theme body,
html.dark-theme #app,
html.dark-theme .bg-white,
html.dark-theme main,
html.dark-theme aside {
  background-color: #0a0a0a !important;
  color: #ffffff !important;
}

html.dark-theme .text-gray-800,
html.dark-theme .text-gray-900,
html.dark-theme .text-gray-700,
html.dark-theme span {
  color: #ffffff !important;
}

html.dark-theme .bg-\[\#F7F7F7\],
html.dark-theme .hover\:bg-\[\#F7F7F7\]:hover,
html.dark-theme .bg-gray-100,
html.dark-theme .bg-gray-50,
html.dark-theme .hover\:bg-gray-50:hover {
  background-color: #1a1a1a !important;
}

html.dark-theme input {
  color: #ffffff !important;
}

html.dark-theme input::placeholder {
  color: #666666 !important;
}

html.dark-theme .border-gray-100 {
  border-color: #333333 !important;
}
</style>


*** Begin Patch
*** Update File: d:\VS\26project\SimpleNote_AI\note-frontend\src\layouts\MainLayout.vue
@@
 html.dark-theme .bg-[#F7F7F7],
 html.dark-theme .hover\:bg-[#F7F7F7]:hover,
 html.dark-theme .bg-gray-100,
 html.dark-theme .bg-gray-50 {
   background-color: #1a1a1a !important;
 }
+
+/* 修复暗色下 hover:bg-gray-50 变成白底的问题 */
+html.dark-theme .hover\:bg-gray-50:hover {
+  background-color: #1a1a1a !important;
+}
*** End Patch
