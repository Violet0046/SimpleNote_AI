<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { RouterView } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AuthModal from '@/components/AuthModal.vue'
import { loginEventBus } from '@/utils/request'

const authStore = useAuthStore()

const handleShowLoginModal = () => {
  authStore.showLoginModal()
}

onMounted(() => {
  loginEventBus.addEventListener('showLoginModal', handleShowLoginModal)
})

onUnmounted(() => {
  loginEventBus.removeEventListener('showLoginModal', handleShowLoginModal)
})
</script>

<template>
  <RouterView />

  <!-- 全局登录弹窗 -->
  <AuthModal
    v-if="authStore.isLoginModalVisible"
    @close="authStore.hideLoginModal"
  />
</template>