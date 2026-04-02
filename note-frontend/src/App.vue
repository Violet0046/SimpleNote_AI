<script setup lang="ts">
import { RouterView } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AuthModal from '@/components/AuthModal.vue'

const authStore = useAuthStore()
</script>

<template>
  <RouterView />

  <!-- 全局登录弹窗 -->
  <AuthModal
    v-if="authStore.isLoginModalVisible"
    @close="authStore.hideLoginModal"
  />
</template>